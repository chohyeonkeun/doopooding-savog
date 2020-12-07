package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import site.jonus.savog.core.exposed.toJavaLocalDate
import java.time.Instant
import java.time.LocalDate

object Pets : LongIdTable("pet", "id") {
    /**
     * 종류
     */
    val type: Column<String> = varchar("type", 30)

    /**
     * 이름
     */
    val name: Column<String> = varchar("name", 30)

    /**
     * 품종
     */
    val breeds: Column<String> = varchar("breeds", 30)

    /**
     * 성별
     */
    val gender: Column<String> = varchar("gender", 15)

    /**
     * 무게
     */
    val weight: Column<Int> = integer("weight").default(0)

    /**
     * 입양 현황
     */
    val adoptionStatus: Column<String> = varchar("adoption_status", 30)

    /**
     * 출생일
     */
    val birthDate: Column<LocalDate> = date("birth_date").toJavaLocalDate()

    /**
     * 생성자
     */
    val creatorId: Column<String> = varchar("creator_id", 30)

    /**
     * 수정자
     */
    val updaterId: Column<String> = varchar("updater_id", 30)

    /**
     * 삭제 여부
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

class Pet(id: EntityID<Long>) : LongEntity(id) {
    var type: String by Pets.type

    var name: String by Pets.name

    var breeds: String by Pets.breeds

    var gender: String by Pets.gender

    var weight: Int by Pets.weight

    var adoptionStatus: String by Pets.adoptionStatus

    var birthDate: LocalDate by Pets.birthDate

    var creatorId: String by Pets.creatorId

    var updaterId: String by Pets.updaterId

    var deleted: Int by Pets.deleted

    var createdAt: Instant by Pets.createdAt

    var updatedAt: Instant by Pets.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<Pet>(Pets)
}