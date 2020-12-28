package site.jonus.savog.api.dto

data class SponsorshipFeeHistoryDto(
    val total: Int,
    val histories: List<SponsorshipFeeHistoryInfo>
)

data class SponsorshipFeeHistoryInfo(
    val id: Long,
    val sponsorshipFeeId: Long,
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val managerId: Long,
    val managerName: String,
    val contentType: String,
    val content: String? = null,
    val creatorId: String,
    val updaterId: String,
    val showOnTop: Boolean? = false,
    val deleted: Boolean? = false,
    val createdAt: Long,
    val updatedAt: Long
)