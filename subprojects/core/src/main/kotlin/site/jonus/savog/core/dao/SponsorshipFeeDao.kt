package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
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
import site.jonus.savog.core.model.Categories
import site.jonus.savog.core.model.Managers
import site.jonus.savog.core.model.Pets
import site.jonus.savog.core.model.SponsorshipFee
import site.jonus.savog.core.model.SponsorshipFeeHistories
import site.jonus.savog.core.model.SponsorshipFees
import site.jonus.savog.core.model.Users

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
        val query = conditions.let { if (it.count() > 0) SponsorshipFees.select(it.compoundAnd()) else SponsorshipFees.selectAll() }

        return query
            .limit(limit, offset)
            .orderBy(SponsorshipFees.id to SortOrder.DESC)
            .map { SponsorshipFee.wrapRow(it) }
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

    fun countHistories(
        sponsorshipFeeIds: List<Long>? = null,
        managerId: Long? = null,
        contentType: String? = null,
        content: String? = null,
        showOnTop: Int? = null,
        deleted: Int? = null
    ): Int {
        val model = SponsorshipFeeHistories
            .join(Managers, JoinType.LEFT, SponsorshipFeeHistories.managerId, Managers.id)
            .join(Users, JoinType.LEFT, Managers.userId, Users.id)
            .join(Categories, JoinType.LEFT, SponsorshipFeeHistories.categoryId, Categories.id)
        val conditions = listOfNotNull(
            sponsorshipFeeIds?.let { SponsorshipFeeHistories.sponsorshipFeeId inList it },
            managerId?.let { Managers.id eq it },
            contentType?.let { SponsorshipFeeHistories.contentType eq it },
            content?.let { SponsorshipFeeHistories.content eq it },
            showOnTop?.let { SponsorshipFeeHistories.showOnTop eq it },
            deleted?.let { SponsorshipFeeHistories.deleted eq it }
        )
        val query = conditions.let { if (it.count() > 0) model.select(it.compoundAnd()) else model.selectAll() }

        return query.count()
    }

    fun searchHistories(
        sponsorshipFeeIds: List<Long>? = null,
        managerId: Long? = null,
        contentType: String? = null,
        content: String? = null,
        showOnTop: Int? = null,
        deleted: Int? = null,
        limit: Int = Constants.Paging.DEFAULT_LIMIT,
        offset: Int = Constants.Paging.DEFAULT_OFFSET
    ): List<Map<String, Any?>> {
        val columns: MutableList<Column<*>> = SponsorshipFeeHistories.columns.toMutableList()
        columns.apply {
            add(Users.nickname)
            add(Categories.name)
        }
        val alias: Map<Expression<*>, String> = mapOf(
            Users.nickname to "managerName",
            Categories.name to "categoryName"
        )
        val model = SponsorshipFeeHistories
            .join(Managers, JoinType.LEFT, SponsorshipFeeHistories.managerId, Managers.id)
            .join(Users, JoinType.LEFT, Managers.userId, Users.id)
            .join(Categories, JoinType.LEFT, SponsorshipFeeHistories.categoryId, Categories.id)
            .slice(columns)
        val conditions = listOfNotNull(
            sponsorshipFeeIds?.let { SponsorshipFeeHistories.sponsorshipFeeId inList it },
            managerId?.let { Managers.id eq it },
            contentType?.let { SponsorshipFeeHistories.contentType eq it },
            content?.let { SponsorshipFeeHistories.content eq it },
            showOnTop?.let { SponsorshipFeeHistories.showOnTop eq it },
            deleted?.let { SponsorshipFeeHistories.deleted eq it }
        )
        val query = conditions.let { if (it.count() > 0) model.select(it.compoundAnd()) else model.selectAll() }

        val orderBy = mutableListOf<Pair<Expression<*>, SortOrder>>()
        if (showOnTop != null && showOnTop == 1) {
            orderBy.add(SponsorshipFeeHistories.showOnTop to SortOrder.DESC)
        }
        orderBy.add(SponsorshipFeeHistories.id to SortOrder.DESC)

        return query
            .limit(limit, offset)
            .orderBy(*orderBy.toTypedArray())
            .map { rowToMap(it, columns, alias) }
    }

    fun createHistory(
        sponsorshipFeeId: Long,
        contentType: String,
        categoryId: Long? = null,
        content: String,
        showOnTop: Boolean = false,
        managerId: Long,
        creatorId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return SponsorshipFeeHistories.insertAndGetId {
            it[this.sponsorshipFeeId] = sponsorshipFeeId
            it[this.contentType] = contentType
            it[this.categoryId] = categoryId
            it[this.content] = content
            it[this.showOnTop] = if (showOnTop) 1 else 0
            it[this.managerId] = managerId
            it[this.creatorId] = creatorId
            it[this.updaterId] = creatorId
        }.value
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
}