package com.savog.doopooding.api

import com.savog.doopooding.api.controller.UploadController
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import java.io.IOException
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

@ExtendWith(SpringExtension::class)
@WebMvcTest(UploadController::class)
@WebAppConfiguration
class RequestUploadTest(private val webApplicationContext: WebApplicationContext) {
    private val logger = LoggerFactory.getLogger(RequestUploadTest::class.simpleName)

    @Test
    fun requestUpload() {
        try {
            val multipartFile = MockMultipartFile("doopooding.png", ClassPathResource("doopooding.png").inputStream)
            val mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
            val result = mockMvc.perform(multipart("/v1/files/upload")
                .file(multipartFile)
                .param("type", "PET_ATTACHMENT")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

            logger.info("file upload test success - ", result)
        } catch (e: IOException) {
            logger.warn("file upload test fail", e)
            throw IOException(e)
        } catch (e: Exception) {
            logger.warn("file upload rest api fail", e)
            throw Exception(e)
        }
    }
}

@Configuration
@ComponentScan
@EnableWebMvc
class WebConfig : WebMvcConfigurationSupport() {
    @Bean
    fun multipartResolver(): MultipartResolver {
        return CommonsMultipartResolver()
    }
}