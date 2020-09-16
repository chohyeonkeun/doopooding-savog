package com.savog.doopooding.core.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.savog.doopooding.core.properties.AwsConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class S3ClientConfiguration {
    @Bean
    @Primary
    fun amazonS3Client(awsConfigurationProperties: AwsConfigurationProperties): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withRegion(awsConfigurationProperties.region.static)
            .apply {
                if (awsConfigurationProperties.credentials.accessKey != null && awsConfigurationProperties.credentials.secretKey != null) {
                    this.withCredentials(
                        AWSStaticCredentialsProvider(
                            BasicAWSCredentials(
                                awsConfigurationProperties.credentials.accessKey,
                                awsConfigurationProperties.credentials.secretKey
                            )
                        )
                    )
                } else {
                    this.withCredentials(WebIdentityTokenCredentialsProvider.create())
                }
            }
            .build()
    }
}
