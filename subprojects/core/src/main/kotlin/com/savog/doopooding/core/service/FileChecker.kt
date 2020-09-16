package com.savog.doopooding.core.service

import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.Property
import org.apache.tika.mime.MediaType
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.image.ImageParser
import org.apache.tika.parser.jpeg.JpegParser
import org.apache.tika.parser.microsoft.OfficeParser
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser
import org.apache.tika.sax.BodyContentHandler
import java.io.File
import java.io.FileInputStream

class FileChecker {
    companion object {
        data class Result(
            val contentType: String,
            val width: Int?,
            val height: Int?
        )

        private val imageParser = ImageParser()
        private val jpagParser = JpegParser()
        private val ooxmlParser = OOXMLParser()
        private val officeParser = OfficeParser()
        private val parser = AutoDetectParser()
            .apply {
                this.parsers = mapOf(
                    MediaType.image("png") to imageParser,
                    MediaType.image("jpeg") to jpagParser,
                    MediaType.application("x-tika-ooxml") to ooxmlParser,
                    MediaType.application("vnd.ms-excel") to officeParser
                )
            }

        fun check(file: File, allowMediaTypes: List<String>? = null): Result {
            val inStream = FileInputStream(file)
            val handler = BodyContentHandler(-1)
            val meta = Metadata()
            val context = ParseContext()
            parser.parse(inStream, handler, meta, context)

            val contentType = meta.get("Content-Type")
            if (allowMediaTypes != null && !allowMediaTypes.contains(contentType)) {
                throw IllegalArgumentException("file upload fail - '$contentType' not allowed.")
            }
            val width = when (contentType) {
                MediaType.image("png").toString() -> meta.getInt(Property.externalInteger("width"))
                MediaType.image("jpeg").toString() -> meta.get("Image Width")?.replace("pixels", "")?.trim()?.toInt()
                else -> null
            }
            val height = when (contentType) {
                MediaType.image("png").toString() -> meta.getInt(Property.externalInteger("height"))
                MediaType.image("jpeg").toString() -> meta.get("Image Height")?.replace("pixels", "")?.trim()?.toInt()
                else -> null
            }
            return Result(contentType, width, height)
        }
    }
}
