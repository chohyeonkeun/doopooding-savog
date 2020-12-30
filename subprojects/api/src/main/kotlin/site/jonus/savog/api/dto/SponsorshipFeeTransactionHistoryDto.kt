package site.jonus.savog.api.dto

data class SponsorshipFeeTransactionHistoryDto(
    val total: Int,
    val transactionHistories: List<TransactionHistoryInfo>
)

data class TransactionHistoryInfo(
    val id: Long,
    val sponsorshipFeeId: Long,
    val transactionType: String,
    val amount: Int,
    val target: String,
    val transactionDate: String,
    val urls: List<String>?,
    val creatorId: String,
    val updaterId: String,
    val createdAt: String,
    val updatedAt: String
)