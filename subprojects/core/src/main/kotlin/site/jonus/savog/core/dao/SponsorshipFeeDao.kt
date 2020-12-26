package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.jonus.savog.core.Constants
import site.jonus.savog.core.model.Pets
import site.jonus.savog.core.model.SponsorshipFee
import site.jonus.savog.core.model.SponsorshipFees

@Repository
@Transactional
class SponsorshipFeeDao : BaseDao() {
    fun count(
        ids: List<Long>? = null,
        petIds: List<Long>? = null,
        status: String? = null,
        creatorId: String? = null
    ): Int {
        val conditions = listOfNotNull(
            ids?.let { SponsorshipFees.id inList it },
            petIds?.let { SponsorshipFees.petId inList it },
            status?.let { SponsorshipFees.status eq it },
            creatorId?.let { SponsorshipFees.creatorId eq it }
        )
        val query = conditions.let { if (it.count() > 0) Pets.select(it.compoundAnd()) else Pets.selectAll() }

        return query.count()
    }

    fun search(
        ids: List<Long>? = null,
        petIds: List<Long>? = null,
        status: String? = null,
        creatorId: String? = null,
        limit: Int = Constants.Paging.DEFAULT_LIMIT,
        offset: Int = Constants.Paging.DEFAULT_OFFSET
    ): List<SponsorshipFee> {
        val conditions = listOfNotNull(
            ids?.let { SponsorshipFees.id inList it },
            petIds?.let { SponsorshipFees.petId inList it },
            status?.let { SponsorshipFees.status eq it },
            creatorId?.let { SponsorshipFees.creatorId eq it },
            SponsorshipFees.deleted eq 0
        )
        val query = conditions.let { if (it.count() > 0) Pets.select(it.compoundAnd()) else Pets.selectAll() }

        return SponsorshipFee.wrapRows(query).toList()
    }

    fun create(
        petId: Long,
        targetAmount: Int,
        status: String,
        creatorId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return SponsorshipFees.insertAndGetId { stmt ->
            stmt[this.petId] = petId
            stmt[this.targetAmount] = targetAmount
            stmt[this.status] = status
            stmt[this.creatorId] = creatorId
            stmt[this.updaterId] = creatorId
        }.value
    }

    fun update(
        id: Long,
        targetAmount: Int? = null,
        status: String? = null,
        deleted: Boolean? = null,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return SponsorshipFees.update({ SponsorshipFees.id eq id }) { stmt ->
            targetAmount?.let { stmt[this.targetAmount] = it }
            status?.let { stmt[this.status] = it }
            deleted?.let { stmt[this.deleted] = if (it) 1 else 0 }
            stmt[this.updaterId] = updaterId
        }
    }

    fun batchDelete(
        targetIds: List<Long>,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return SponsorshipFees.update({ SponsorshipFees.id inList targetIds }) {
            it[this.deleted] = 1
            it[this.updaterId] = updaterId
        }
    }

    fun findById(id: Long): SponsorshipFee {
        return SponsorshipFee[id]
    }

    fun findSponsorshipFeeToMap(id: Long): Map<String, Any?>? {
        return SponsorshipFees
            .select { SponsorshipFees.id eq id }
            .limit(1)
            .map { rowToMap(it, SponsorshipFees.columns, mapOf()) }
            .firstOrNull()
    }
}