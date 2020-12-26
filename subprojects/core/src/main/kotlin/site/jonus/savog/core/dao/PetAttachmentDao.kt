package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.jonus.savog.core.Constants
import site.jonus.savog.core.model.PetAttachment
import site.jonus.savog.core.model.PetAttachments

@Repository
@Transactional
class PetAttachmentDao : BaseDao() {
    fun findPetAttachmentByPetIds(petIds: List<Long>): List<PetAttachment> {
        val query = PetAttachments
            .select { PetAttachments.petId inList petIds }
            .andWhere { PetAttachments.deleted eq 0 }
            .orderBy(PetAttachments.id to SortOrder.DESC)

        return PetAttachment.wrapRows(query).toList()
    }

    fun getPetAttachment(id: Long): PetAttachment? {
        return PetAttachment[id]
    }

    fun createPetAttachment(
        petId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        creatorId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return PetAttachments.insertAndGetId {
            it[this.petId] = petId
            it[this.type] = type
            if (bucket != null) it[this.bucket] = bucket
            if (key != null) it[this.key] = key
            it[this.filename] = filename
            it[this.creatorId] = creatorId
            it[this.updaterId] = creatorId
        }.value
    }

    fun updatePetAttachment(
        id: Long,
        petId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return PetAttachments.update(
            {
                listOfNotNull(
                    PetAttachments.id eq id,
                    PetAttachments.petId eq petId
                ).compoundAnd()
            },
            limit = 1
        ) {
            it[this.type] = type
            if (bucket != null) it[this.bucket] = bucket
            if (key != null) it[this.key] = key
            it[this.filename] = filename
            it[this.updaterId] = updaterId
        }
    }

    fun upsertPetAttachment(
        petId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        id: Long? = null,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return when (id?.let { getPetAttachment(it) }) {
            null -> createPetAttachment(petId, type, bucket, key, filename, updaterId)
            else -> updatePetAttachment(id, petId, type, bucket, key, filename, updaterId).toLong()
        }
    }

    fun deletePetAttachment(ids: List<Long>, updaterId: String = Constants.SYSTEM_USERNAME): Int {
        return PetAttachments.update({ (PetAttachments.id inList ids) }) {
            it[this.deleted] = 1
            it[this.updaterId] = updaterId
        }
    }
}