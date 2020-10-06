package site.jonus.savog.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cloud.aws")
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