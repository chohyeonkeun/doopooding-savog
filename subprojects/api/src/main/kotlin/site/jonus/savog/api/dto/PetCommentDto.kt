package site.jonus.savog.api.dto

data class PetCommentDto(
    val total: Int,
    val comments: List<PetCommentInfo>
)

data class PetCommentInfo(
    val commentId: Long? = null,
    val petId: Long? = null,
    val userId: Long? = null,
    val parentId: Long? = null,
    val comment: String? = null,
    val showOnTop: Boolean? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)