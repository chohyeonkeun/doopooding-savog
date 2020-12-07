package site.jonus.savog.core

object Constants {
    const val SERVICE_NAME = "savog"
    const val DEBUG = true

    const val SYSTEM_USERNAME = "system"

    object Paging {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 30
    }

    enum class LoggingType {
        INFO,
        WARN,
        ERROR
    }
}