package site.jonus.savog.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import site.jonus.savog.core.service.FileUploadService
import site.jonus.savog.core.util.UploadConfig
import java.io.IOException

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
//@Import(S3MockConfig::class)
class FileUploadServiceTest {
    private val logger = LoggerFactory.getLogger(FileUploadServiceTest::class.simpleName)

    @Autowired
    private lateinit var fileUploadService: FileUploadService

//    @Autowired
//    private lateinit var s3Mock: S3Mock

    @Test
    fun requestUpload() {
        try {
            val multipartFile = MockMultipartFile("doopooding.png", ClassPathResource("doopooding.png").inputStream)
            val type = "PET_ATTACHMENT"
            val allowTypes = UploadConfig.getType(type).allowTypes
            val s3Object = fileUploadService.upload(multipartFile, type, allowMediaTypes = allowTypes)
            val signedUrl = fileUploadService.getSignedUrl(s3Object.bucket, s3Object.key)
            assertThat(s3Object.bucket).isEqualTo("savog-pet-upload")
            assertThat(signedUrl).isNotNull()

            logger.info("file upload test success")
        } catch (e: IOException) {
            logger.warn("file upload test fail", e)
            throw IOException(e)
        } catch (e: Exception) {
            logger.warn("file upload rest api fail", e)
            throw Exception(e)
        }
    }

//    @After
//    fun shutdownMockS3() {
//        s3Mock.stop()
//    }
}
