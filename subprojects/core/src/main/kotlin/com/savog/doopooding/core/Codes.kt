package com.savog.doopooding.core

object Codes {
    enum class LoginType(val label: String, val value: String) {
        SNS_NAVER("SNS 네이버", "LOGTP_NAVER"),

        EMAIL("이메일", "LOGTP_EMAIL");

        companion object {
            fun getByValue(value: String): LoginType? = values().find { it.value == value }
        }
    }
}