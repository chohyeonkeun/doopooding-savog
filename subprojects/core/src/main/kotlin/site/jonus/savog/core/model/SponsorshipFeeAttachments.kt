package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object SponsorshipFeeAttachments : LongIdTable("sponsorship_fee_attachment", "id") {
    /**
     * 후원금 ID
     */
    val sponsorshipFeeId: Column<Long> = long("sponsorship_fee_id")

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

class SponsorshipFeeAttachment(id: EntityID<Long>) : LongEntity(id) {
    var sponsorshipFeeId: Long by SponsorshipFeeAttachments.sponsorshipFeeId

    var type: String by SponsorshipFeeAttachments.type

    var bucket: String by SponsorshipFeeAttachments.bucket

    var key: String by SponsorshipFeeAttachments.key

    var filename: String by SponsorshipFeeAttachments.filename

    var memo: String? by SponsorshipFeeAttachments.memo

    var creatorId: String by SponsorshipFeeAttachments.creatorId

    var updaterId: String by SponsorshipFeeAttachments.updaterId

    var deleted: Int by SponsorshipFeeAttachments.deleted

    var createdAt: Instant by SponsorshipFeeAttachments.createdAt

    var updatedAt: Instant by SponsorshipFeeAttachments.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<SponsorshipFeeAttachment>(SponsorshipFeeAttachments)
}