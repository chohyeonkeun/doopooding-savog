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
        MASTER("마스터", "UROTP_MASTER"),

        ADMIN("관리자", "UROTP_ADMIN"),

        GENERAL("일반회원", "UROTP_GENERAL");

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