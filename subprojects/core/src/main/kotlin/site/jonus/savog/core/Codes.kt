package site.jonus.savog.core

object Codes {
    enum class LoginType(val label: String, val value: String) {
        SNS_NAVER("SNS 네이버", "LOGTP_NAVER"),

        EMAIL("이메일", "LOGTP_EMAIL");

        companion object {
            fun getByValue(value: String): LoginType? = values().find { it.value == value }
        }
    }
    enum class UserRoleType(val label: String, val value: String) {
        ADMIN("관리자", "ROLE_ADMIN"),

        MANAGER("운영자", "ROLE_MANAGER"),

        USER("일반회원", "ROLE_USER");

        companion object {
            fun getByValue(value: String): UserRoleType? = values().find { it.value == value }
        }
    }

    enum class HistoryContentType(val label: String, val value: String) {
        CHANGE_LOG("변경이력", "HICTY_CHANGE_LOG");

        companion object {
            fun getByValue(value: String): HistoryContentType? = values().find { it.value == value }
        }
    }
}