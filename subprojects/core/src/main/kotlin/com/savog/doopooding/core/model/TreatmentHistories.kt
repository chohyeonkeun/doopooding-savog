package com.savog.doopooding.core.model

import com.savog.doopooding.core.exposed.toJavaInstant
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant

object TreatmentHistories : LongIdTable("treatment_history", "id") {
    /**
     * 애완동물 ID
     */
    val petId: Column<Long> = long("pet_id")

    /**
     * 치료내용
     */
    val contents: Column<String> = varchar("contents", 150)

    /**
     * 치료일자
     */
    val treatmentDate: Column<Instant> = datetime("treatment_date").toJavaInstant()

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

class TreatmentHistory(id: EntityID<Long>) : LongEntity(id) {
    var petId: Long by TreatmentHistories.petId

    var contents: String by TreatmentHistories.contents

    var treatmentDate: Instant by TreatmentHistories.treatmentDate

    var creatorId: String by TreatmentHistories.creatorId

    var updaterId: String by TreatmentHistories.updaterId

    var deleted: Int by TreatmentHistories.deleted

    var createdAt: Instant by TreatmentHistories.createdAt

    var updaterdAt: Instant by TreatmentHistories.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<TreatmentHistory>(TreatmentHistories)
}