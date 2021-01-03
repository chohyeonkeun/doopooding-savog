package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.jonus.savog.core.Constants
import site.jonus.savog.core.model.PetDisease
import site.jonus.savog.core.model.PetDiseases

@Repository
@Transactional
class PetDiseaseDao : BaseDao() {
    fun countPetDiseasesByPetId(petId: Long): Int {
        return PetDiseases
            .select { PetDiseases.petId eq petId }
            .andWhere { PetDiseases.deleted eq 0 }
            .count()
    }

    fun findPetDiseasesByPetId(petId: Long): List<PetDisease> {
        return PetDiseases
            .select { PetDiseases.petId eq petId }
            .andWhere { PetDiseases.deleted eq 0 }
            .orderBy(PetDiseases.healed to SortOrder.DESC)
            .map {
                PetDisease.wrapRow(it)
            }
    }

    fun findPetDiseaseToMap(id: Long): Map<String, Any?>? {
        return PetDiseases
            .select { PetDiseases.id eq id }
            .limit(1)
            .map { rowToMap(it, PetDiseases.columns, mapOf()) }
            .firstOrNull()
    }

    fun create(
        petId: Long,
        name: String,
        healed: Boolean,
        creatorId: String
    ): Long {
        return PetDiseases.insertAndGetId { stmt ->
            stmt[this.petId] = petId
            stmt[this.name] = name
            stmt[this.healed] = if (healed) 1 else 0
            stmt[this.creatorId] = creatorId
            stmt[this.updaterId] = creatorId
        }.value
    }

    fun update(
        diseaseId: Long,
        name: String? = null,
        healed: Boolean? = null,
        deleted: Boolean? = null,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return PetDiseases.update({ PetDiseases.id eq diseaseId }) { stmt ->
            name?.let { stmt[this.name] = it }
            healed?.let { stmt[this.healed] = if (it) 1 else 0 }
            deleted?.let { stmt[this.deleted] = if (it) 1 else 0 }
            stmt[this.updaterId] = updaterId
        }
    }
}