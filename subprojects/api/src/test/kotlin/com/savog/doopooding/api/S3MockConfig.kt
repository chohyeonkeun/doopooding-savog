package com.savog.doopooding.api

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import io.findify.s3mock.S3Mock
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class S3MockConfig {

    @Value("cloud.aws.region.static")
    lateinit var region: String

    @Value("cloud.aws.s3.bucket")
    lateinit var bucket: String

    @Bean
    fun s3Mock(): S3Mock {
        return S3Mock.Builder().withPort(8001).withInMemoryBackend().build()
    }

    @Bean
    @Primary
    fun amazonS3(s3Mock: S3Mock): AmazonS3 {
        s3Mock.start()
        val endpoint = AwsClientBuilder.EndpointConfiguration("http://localhost:8001", region)
        val client = AmazonS3ClientBuilder
            .standard()
            .withPathStyleAccessEnabled(true)
            .withEndpointConfiguration(endpoint)
            .withCredentials(AWSStaticCredentialsProvider(AnonymousAWSCredentials()))
            .build()
        client.createBucket(bucket)

        return client
    }
}
