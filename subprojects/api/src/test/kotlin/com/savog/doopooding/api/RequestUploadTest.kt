package com.savog.doopooding.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest
class RequestUploadTest(private val mvc: MockMvc, private val objectMapper: ObjectMapper) {
    private val logger = LoggerFactory.getLogger(RequestUploadTest::class.simpleName)

    @Test
    fun requestUpload() {
        try {
            val multipartFile = MockMultipartFile("doopooding.png", ClassPathResource("doopooding.png").inputStream)
            val data = mapOf(
                "type" to "PET_ATTACHMENT",
                "file" to multipartFile
            )
            val content = objectMapper.writeValueAsString(data)

            val result = mvc.perform(post("/v1/files/upload")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

            logger.info("file upload test success - ", objectMapper.readValue(result))
        } catch (e: IOException) {
            logger.warn("file upload test fail", e)
            throw IOException(e)
        } catch (e: Exception) {
            logger.warn("file upload rest api fail", e)
            throw Exception(e)
        }
    }
}
