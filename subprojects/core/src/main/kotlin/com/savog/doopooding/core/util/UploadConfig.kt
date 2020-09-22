package com.savog.doopooding.core.util

object UploadConfig {
    enum class Extensions(vararg val extensions: String) {
        EXCEL(".xls", ".xlsx"),
        IMAGE(".jpg", ".jpeg", ".png"),
        PDF(".pdf"),
        ZIP(".zip");

        fun mediaTypes(): List<String> {
            return extensions.mapNotNull { ext ->
                when (ext) {
                    ".png" -> "image/png"
                    ".jpg" -> "image/jpeg"
                    ".xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    ".xls" -> "application/vnd.ms-excel"
                    ".pdf" -> "application/pdf"
                    ".zip" -> "application/zip"
                    else -> null
                }
            }
        }
    }

    enum class Type(vararg extensions: Extensions) {
        DEFAULT(Extensions.IMAGE, Extensions.PDF, Extensions.EXCEL, Extensions.ZIP),
        PET_ATTACHMENT(Extensions.IMAGE, Extensions.PDF, Extensions.EXCEL, Extensions.ZIP),
        BOARD_ATTACHMENT(Extensions.IMAGE, Extensions.PDF, Extensions.EXCEL, Extensions.ZIP);

        val allowTypes = extensions.map { it.mediaTypes() }.flatten()
    }

    fun getType(type: String): Type {
        return Type.valueOf(type.toUpperCase())
    }
}
