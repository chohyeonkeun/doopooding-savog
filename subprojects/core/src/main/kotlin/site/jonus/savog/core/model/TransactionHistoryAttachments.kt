package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object TransactionHistoryAttachments : LongIdTable("transaction_history_attachment", "id") {
    /**
     * 후원금 내역 ID
     */
    val sponsorshipFeeTransactionHistoryId: Column<Long> = long("sponsorship_fee_transaction_history_id")

    /**
     * 첨부파일 유형
     */
    val type: Column<String> = varchar("type", 50)

    /**
     * S3 버킷
     */
    val bucket: Column<String> = varchar("bucket", 100)

    /**
     * S3
     */
    val key: Column<String> = varchar("key", 100)

    /**
     * 파일명
     */
    val filename: Column<String> = varchar("filename", 100)

    /**
     * 메모
     */
    val memo: Column<String?> = text("memo").nullable()

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

class TransactionHistoryAttachment(id: EntityID<Long>) : LongEntity(id) {
    var sponsorshipFeeTransactionHistoryId: Long by TransactionHistoryAttachments.sponsorshipFeeTransactionHistoryId

    var type: String by TransactionHistoryAttachments.type

    var bucket: String by TransactionHistoryAttachments.bucket

    var key: String by TransactionHistoryAttachments.key

    var filename: String by TransactionHistoryAttachments.filename

    var memo: String? by TransactionHistoryAttachments.memo

    var creatorId: String by TransactionHistoryAttachments.creatorId

    var updaterId: String by TransactionHistoryAttachments.updaterId

    var deleted: Int by TransactionHistoryAttachments.deleted

    var createdAt: Instant by TransactionHistoryAttachments.createdAt

    var updatedAt: Instant by TransactionHistoryAttachments.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<TransactionHistoryAttachment>(TransactionHistoryAttachments)
}