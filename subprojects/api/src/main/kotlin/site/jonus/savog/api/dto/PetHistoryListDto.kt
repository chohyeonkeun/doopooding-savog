package site.jonus.savog.api.dto

data class PetHistoryListDto(
    val total: Int? = 0,
    val id: Long? = 0,
    val petId: Long? = 0,
    val categoryId: Long? = 0,
    val managerId: Long? = 0,
    val contentType: String? = "",
    val content: String? = "",
    val creatorId: String? = "",
    val updaterId: String? = "",
    val showOnTop: Boolean? = false,
    val deleted: Boolean? = false,
    val createdAt: Long? = 0,
    val updatedAt: Long? = 0
)