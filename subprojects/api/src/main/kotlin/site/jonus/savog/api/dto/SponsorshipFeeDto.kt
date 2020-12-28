package site.jonus.savog.api.dto

data class SponsorshipFeeDto(
    val total: Int,
    val sponsorshipFees: List<SponsorshipFeeInfo>
)

data class SponsorshipFeeInfo(
    val id: Long,
    val petId: Long,
    val targetAmount: Int,
    val status: String,
    val petAttachmentUrls: List<String>,
    val creatorId: String,
    val updaterId: String,
    val createdAt: Long,
    val updatedAt: Long
)