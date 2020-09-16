package com.savog.doopooding.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("aws")
class AwsConfigurationProperties {
    val credentials = Credentials()
    val region = Region()
    class Credentials {
        var accessKey: String? = null
        var secretKey: String? = null
    }
    class Region {
        var auto: Boolean = false
        lateinit var static: String
    }
}