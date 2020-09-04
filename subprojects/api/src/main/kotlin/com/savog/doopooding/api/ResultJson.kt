package com.savog.doopooding.api

data class ResultJson(
    val success: Boolean,
    val data: Any? = null,
    val errors: List<Error>? = null
) {
    data class Error(
        val code: String,
        val message: String? = null
    )

    companion object {
        fun withData(data: Any): ResultJson {
            return ResultJson(true, data)
        }

        fun withError(vararg errors: Error, data: Any? = null): ResultJson {
            return ResultJson(
                false,
                data,
                errors = errors.toList()
            )
        }
    }
}