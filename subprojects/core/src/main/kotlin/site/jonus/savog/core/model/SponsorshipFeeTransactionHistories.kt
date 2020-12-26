package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object SponsorshipFeeTransactionHistories : LongIdTable("sponsorship_fee_transaction_history", "id") {
    /**
     * 후원금 ID
     */
    val sponsorshipFeeId: Column<Long> = long("sponsorship_fee_id")

    /**
     * 거래 종류 (입/출금)
     */
    val transactionType: Column<String> = varchar("transaction_type", 30)

    /**
     * 거래금액
     */
    val amount: Column<Int> = integer("amount")

    /**
     * 거래대상자
     */
    val target: Column<String> = varchar("target", 45)

    /**
     * 거래일시
     */
    val transactionDate: Column<Instant> = datetime("transaction_date").toJavaInstant()

    /**
     * 생성자
     */
    val creatorId: Column<String> = varchar("creator_id", 30)

    /**
     * 수정자
     */
    val updaterId: Column<String> = varchar("updater_id", 30)

    /**
     * 삭제여부
     */
    val deleted: Column<Int> = integer("deleted").default(0)

    /**
     * 생성일
     */
    val createdAt: Column<Instant> = datetime("created_at").toJavaInstant()

    /**
     * 수정일
     */
    val updatedAt: Column<Instant> = datetime("updated_at").toJavaInstant()
}

class SponsorshipFeeTransactionHistory(id: EntityID<Long>) : LongEntity(id) {
    var sponsorshipFeeId: Long by SponsorshipFeeTransactionHistories.sponsorshipFeeId

    var transactionType: String by SponsorshipFeeTransactionHistories.transactionType

    var amount: Int by SponsorshipFeeTransactionHistories.amount

    var target: String by SponsorshipFeeTransactionHistories.target

    var transactionDate: Instant by SponsorshipFeeTransactionHistories.transactionDate

    var creatorId: String by SponsorshipFeeTransactionHistories.creatorId

    var updaterId: String by SponsorshipFeeTransactionHistories.updaterId

    var deleted: Int by SponsorshipFeeTransactionHistories.deleted

    var createdAt: Instant by SponsorshipFeeTransactionHistories.createdAt

    var updatedAt: Instant by SponsorshipFeeTransactionHistories.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<SponsorshipFeeTransactionHistory>(SponsorshipFeeTransactionHistories)
}