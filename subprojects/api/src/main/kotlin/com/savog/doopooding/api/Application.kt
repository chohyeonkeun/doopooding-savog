package com.savog.doopooding.api

import com.savog.doopooding.client.login.SnsClientProperties
import com.savog.doopooding.core.properties.AwsConfigurationProperties
import com.savog.doopooding.core.properties.S3ConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
@EnableConfigurationProperties(
    SnsClientProperties::class,
    S3ConfigurationProperties::class,
    AwsConfigurationProperties::class
)
@ComponentScan(
    basePackages = [
        "com.savog.doopooding.api",
        "com.savog.doopooding.core",
        "com.savog.doopooding.api.configuration",
        "com.savog.doopooding.client.login"
    ]
)
class SavogApiServer : WebMvcConfigurer {
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        // Accept 헤더가 따로 주어지지 않았을 때 기본 응답을 JSON으로 한다.
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
    }
}

fun main(args: Array<String>) {
    runApplication<SavogApiServer>(*args)
}