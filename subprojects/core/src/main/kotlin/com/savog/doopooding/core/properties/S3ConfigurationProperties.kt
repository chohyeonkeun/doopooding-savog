package com.savog.doopooding.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("aws.s3")
class S3ConfigurationProperties {
    val temporalBucket = S3BucketConfiguration()
    val buckets: List<S3BucketConfiguration> = mutableListOf()

    class S3BucketConfiguration {
        lateinit var bucketName: String
        lateinit var type: String
        var external: Boolean = false
    }
}