package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object SponsorshipFeeHistories : LongIdTable("sponsorship_fee_history", "id") {
    /**
     * 후원금 ID
     */
    val sponsorshipFeeId: Column<Long> = long("sponsorship_fee_id")

    /**
     * 카테고리 ID
     */
    val categoryId: Column<Long?> = long("category_id").nullable()

    /**
     * 관리자 ID
     */
    val managerId: Column<Long> = long("manager_id")

    /**
     * 히스토리 유형
     */
    val contentType: Column<String> = varchar("content_type", 50)

    /**
     * 변경내용
     */
    val content: Column<String> = text("content")

    /**
     * 생성자 ID
     */
    val creatorId: Column<String> = varchar("creator_id", 32)

    /**
     * 갱신자 ID
     */
    val updaterId: Column<String> = varchar("updater_id", 32)

    /**
     * 공지 여부
     */
    val showOnTop: Column<Int> = integer("show_on_top").default(0)

    /**
     * 삭제 여부
     */
    val deleted: Column<Int> = integer("deleted").default(0)

    /**
     * 생성일
     */
    val createdAt: Column<Instant> = datetime("created_at").toJavaInstant()

    /**
     * 갱신일
     */
    val updatedAt: Column<Instant> = datetime("updated_at").toJavaInstant()
}

class SponsorshipFeeHistory(id: EntityID<Long>) : LongEntity(id) {
    var sponsorshipFeeId: Long by SponsorshipFeeHistories.sponsorshipFeeId

    var categoryId: Long? by SponsorshipFeeHistories.categoryId

    var managerId: Long by SponsorshipFeeHistories.managerId

    var contentType: String by SponsorshipFeeHistories.contentType

    var content: String by SponsorshipFeeHistories.content

    var creatorId: String by SponsorshipFeeHistories.creatorId

    var updaterId: String by SponsorshipFeeHistories.updaterId

    var showOnTop: Int by SponsorshipFeeHistories.showOnTop

    var deleted: Int by SponsorshipFeeHistories.deleted

    var createdAt: Instant by SponsorshipFeeHistories.createdAt

    var updatedAt: Instant by SponsorshipFeeHistories.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<SponsorshipFeeHistory>(SponsorshipFeeHistories)
}