package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.jonus.savog.core.Constants
import site.jonus.savog.core.model.PetTreatmentHistories
import site.jonus.savog.core.model.PetTreatmentHistory
import java.time.LocalDate

@Repository
@Transactional
class PetTreatmentHistoryDao : BaseDao() {
    fun countPetTreatmentHistoriesByPetId(petId: Long): Int {
        return PetTreatmentHistories
            .select { PetTreatmentHistories.petId eq petId }
            .andWhere { PetTreatmentHistories.deleted eq 0 }
            .count()
    }

    fun findPetTreatmentHistoriesByPetId(petId: Long): List<PetTreatmentHistory> {
        return PetTreatmentHistories
            .select { PetTreatmentHistories.petId eq petId }
            .andWhere { PetTreatmentHistories.deleted eq 0 }
            .orderBy(PetTreatmentHistories.treatmentDate to SortOrder.DESC)
            .map { PetTreatmentHistory.wrapRow(it) }
    }

    fun findPetTreatmentHistoriesByDiseaseId(petDiseaseId: Long): List<PetTreatmentHistory> {
        return PetTreatmentHistories
            .select { PetTreatmentHistories.petDiseaseId eq petDiseaseId }
            .andWhere { PetTreatmentHistories.deleted eq 0 }
            .orderBy(PetTreatmentHistories.treatmentDate to SortOrder.DESC)
            .map { PetTreatmentHistory.wrapRow(it) }
    }

    fun create(
        petId: Long,
        petDiseaseId: Long? = null,
        contents: String,
        treatmentDate: LocalDate,
        creatorId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return PetTreatmentHistories.insertAndGetId { stmt ->
            stmt[this.petId] = petId
            petDiseaseId?.let { stmt[this.petDiseaseId] = it }
            stmt[this.contents] = contents
            stmt[this.treatmentDate] = treatmentDate
            stmt[this.creatorId] = creatorId
            stmt[this.updaterId] = creatorId
        }.value
    }

    fun update(
        id: Long,
        contents: String? = null,
        treatmentDate: LocalDate? = null,
        deleted: Boolean? = null,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return PetTreatmentHistories.update({ PetTreatmentHistories.id eq id }) { stmt ->
            contents?.let { stmt[this.contents] = it }
            treatmentDate?.let { stmt[this.treatmentDate] = it }
            deleted?.let { stmt[this.deleted] = if (it) 1 else 0 }
            stmt[this.updaterId] = updaterId
        }
    }
}