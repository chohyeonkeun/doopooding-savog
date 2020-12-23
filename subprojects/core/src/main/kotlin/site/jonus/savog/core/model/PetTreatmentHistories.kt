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

object PetTreatmentHistories : LongIdTable("pet_treatment_history", "id") {
    /**
     * 애완동물 ID
     */
    val petId: Column<Long> = long("pet_id")

    /**
     * 질병 ID
     */
    val petDiseaseId: Column<Long> = long("pet_disease_id")

    /**
     * 치료내용
     */
    val contents: Column<String> = text("contents")

    /**
     * 치료일자
     */
    val treatmentDate: Column<LocalDate> = date("treatment_date").toJavaLocalDate()

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

class PetTreatmentHistory(id: EntityID<Long>) : LongEntity(id) {
    var petId: Long by PetTreatmentHistories.petId

    var petDiseaseId: Long by PetTreatmentHistories.petDiseaseId

    var contents: String by PetTreatmentHistories.contents

    var treatmentDate: LocalDate by PetTreatmentHistories.treatmentDate

    var creatorId: String by PetTreatmentHistories.creatorId

    var updaterId: String by PetTreatmentHistories.updaterId

    var deleted: Int by PetTreatmentHistories.deleted

    var createdAt: Instant by PetTreatmentHistories.createdAt

    var updaterdAt: Instant by PetTreatmentHistories.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<PetTreatmentHistory>(PetTreatmentHistories)
}