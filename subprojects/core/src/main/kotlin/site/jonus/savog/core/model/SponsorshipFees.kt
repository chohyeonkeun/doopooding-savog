package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object SponsorshipFees : LongIdTable("sponsorship_fee", "id") {
    /**
     * 애완동물 ID
     */
    val petId: Column<Long> = long("pet_id")

    /**
     * 목표 금액
     */
    val targetAmount: Column<Int> = integer("target_amount")

    /**
     * 후원금 현황
     */
    val status: Column<String> = varchar("status", 30)

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

class SponsorshipFee(id: EntityID<Long>) : LongEntity(id) {
    var petId: Long by SponsorshipFees.petId

    var targetAmount: Int by SponsorshipFees.targetAmount

    var status: String by SponsorshipFees.status

    var creatorId: String by SponsorshipFees.creatorId

    var updaterId: String by SponsorshipFees.updaterId

    var deleted: Int by SponsorshipFees.deleted

    var createdAt: Instant by SponsorshipFees.createdAt

    var updatedAt: Instant by SponsorshipFees.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<SponsorshipFee>(SponsorshipFees)
}