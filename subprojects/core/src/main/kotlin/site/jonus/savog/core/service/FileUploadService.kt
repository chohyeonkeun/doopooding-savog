package site.jonus.savog.core.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CopyObjectRequest
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.ResponseHeaderOverrides
import com.google.common.base.CaseFormat
import com.google.common.io.Files
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import site.jonus.savog.core.properties.S3ConfigurationProperties
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

@Service
class FileUploadService(
    private val s3Config: S3ConfigurationProperties,
    private val amazonS3Client: AmazonS3,
    private val amazonS3External: AmazonS3
) {
    fun upload(file: MultipartFile, type: String = "default", allowMediaTypes: List<String>? = null, isDev: Boolean = false): S3Object {
        val key = generateKey(type, file.originalFilename!!, isDev)
        return uploadWithKey(file, key, allowMediaTypes)
    }

    fun uploadWithKey(file: MultipartFile, key: String, allowMediaTypes: List<String>? = null): S3Object {
        return uploadWithKey(file.inputStream, file.size, key, allowMediaTypes)
    }

    fun uploadWithKey(inputStream: InputStream, size: Long, key: String, allowMediaTypes: List<String>? = null): S3Object {
        val file = File.createTempFile("upload", Instant.now().epochSecond.toString())
        val outputStream = FileOutputStream(file)
        outputStream.use {
            inputStream.copyTo(it)
        }

        val result = FileChecker.check(file, allowMediaTypes)
        val metadata = ObjectMetadata()
        metadata.contentType = result.contentType
        metadata.contentLength = size
        FileInputStream(file).use {
            amazonS3Client.putObject(PutObjectRequest(s3Config.temporalBucket.bucketName, key, it, metadata))
        }

        try { file.delete() } catch (e: Exception) {}

        return S3Object(s3Config.temporalBucket.bucketName, key, result.width, result.height)
    }

    fun preserve(type: String, sourceKey: String): S3Object {
        return preserve(type, s3Config.temporalBucket.bucketName, sourceKey)
    }

    fun preserve(type: String, sourceBucket: String, sourceKey: String): S3Object {
        val obj = amazonS3Client.getObject(sourceBucket, sourceKey)
        val meta = obj.objectMetadata
        obj.close()
        return copyTo(sourceBucket, sourceKey, findBucket(type), sourceKey, type, meta)
    }

    fun copyTo(sourceBucket: String, srcFileKey: String, destinationBucket: String, destinationKey: String, type: String, meta: ObjectMetadata? = null): S3Object {
        val bucket = findBucketInfo(type)
        val request = CopyObjectRequest(sourceBucket, srcFileKey, destinationBucket, destinationKey)
            .also {
                if (meta != null) it.withNewObjectMetadata(meta)
            }
        if (bucket.external) {
            val obj = amazonS3Client.getObject(sourceBucket, srcFileKey)
            val inStream = obj.objectContent
            inStream.use {
                amazonS3External.putObject(
                    PutObjectRequest(destinationBucket, destinationKey, it, meta)
                )
            }
            obj.close()
        } else {
            amazonS3Client.copyObject(request)
        }

        return S3Object(destinationBucket, destinationKey)
    }

    fun getSignedUrl(bucket: String, key: String, filename: String? = null): URL {
        val now = Instant.now()
        val expiration = now + Duration.ofHours(1)

        val request = GeneratePresignedUrlRequest(bucket, key)
        request.expiration = Date.from(expiration)

        if (filename != null) {
            val overrides = ResponseHeaderOverrides()
            val encoded = URLEncoder.encode(filename, "UTF-8")
            overrides.contentDisposition = "attachment; filename=\"$encoded\""
            request.responseHeaders = overrides
        }

        return amazonS3Client.generatePresignedUrl(request)
    }

    fun findBucket(type: String): String {
        val bucketType = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, type)
        val defaultBucket = s3Config.buckets.find { it.type == "DEFAULT" }!!.bucketName
        return s3Config.buckets.find { it.type == bucketType }?.bucketName ?: defaultBucket
    }

    fun findBucketInfo(type: String): S3ConfigurationProperties.S3BucketConfiguration {
        val bucketType = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, type)
        val defaultBucket = s3Config.buckets.find { it.type == "DEFAULT" }!!
        return s3Config.buckets.find { it.type == bucketType } ?: defaultBucket
    }

    fun generateKey(prefix: String = "", filename: String, isDev: Boolean = false): String {
        val extension = Files.getFileExtension(filename)
        val uuid = UUID.randomUUID()
        val now = LocalDateTime.now()
        val name = DateTimeFormatter.ofPattern("yyyyMMdd/kk_mm_ss").format(now)

        val result = if (prefix.isNotEmpty()) {
            "$prefix/$name-$uuid.$extension"
        } else {
            "$name-$uuid.$extension"
        }
        return if (isDev) {
            "development/$result"
        } else {
            result
        }
    }

    fun getObject(bucket: String, key: String): com.amazonaws.services.s3.model.S3Object {
        return amazonS3Client.getObject(bucket, key)
    }
}

data class S3Object(
    val bucket: String,
    val key: String,
    val width: Int? = null,
    val height: Int? = null
) {
    fun toS3URI(): URI {
        return URI("s3", bucket, "/${key.trimStart('/')}", null)
    }

    companion object {
        fun fromS3URI(uri: URI): S3Object {
            return S3Object(bucket = uri.host, key = uri.path.trimStart('/'))
        }
    }
}
