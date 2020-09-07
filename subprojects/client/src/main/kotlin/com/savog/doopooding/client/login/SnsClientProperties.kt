package com.savog.doopooding.client.login

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("sns-login-key")
class SnsClientProperties {
    val naver = Naver()

    class Naver {
        lateinit var clientId: String
        lateinit var clientSecret: String
    }
}