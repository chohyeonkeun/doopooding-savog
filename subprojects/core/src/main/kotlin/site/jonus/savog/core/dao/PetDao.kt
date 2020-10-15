package site.jonus.savog.core.dao

import site.jonus.savog.core.Constants
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
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
import site.jonus.savog.core.model.Categories
import site.jonus.savog.core.model.Managers
import site.jonus.savog.core.model.Pet
import site.jonus.savog.core.model.PetHistories
import site.jonus.savog.core.model.Users

@Repository
@Transactional
class PetDao : BaseDao() {
    fun create() {
    }

    fun search() {
    }

    fun count() {
    }

    fun update() {
    }

    fun delete() {
    }

    fun findById(id: Long): Pet {
        return Pet[id]
    }

    fun countHistories() {
    }

    fun searchHistories(
        petIds: List<Long>? = null,
        managerId: Long? = null,
        contentType: String? = null,
        content: String? = null,
        showOnTop: Int? = null,
        deleted: Int? = null,
        limit: Int = 50,
        offset: Int = 0
    ): List<Map<String, Any?>> {
        val columns: MutableList<Column<*>> = PetHistories.columns.toMutableList()
        columns.apply {
            add(Users.nickname)
            add(Categories.name)
        }
        val alias: Map<Expression<*>, String> = mapOf(
            Users.nickname to "managerName",
            Categories.name to "categoryName"
        )
        val model = PetHistories
            .join(Managers, JoinType.LEFT, PetHistories.managerId, Managers.id)
            .join(Users, JoinType.LEFT, Managers.userId, Users.id)
            .join(Categories, JoinType.LEFT, PetHistories.categoryId, Categories.id)
            .slice(columns)
        val conditions = listOf(
            petIds?.let { PetHistories.petId inList it },
            managerId?.let { Managers.id eq it },
            contentType?.let { PetHistories.contentType eq it },
            content?.let { PetHistories.content eq it },
            showOnTop?.let { PetHistories.showOnTop eq it },
            deleted?.let { PetHistories.deleted eq it }
        )
        val query = conditions.let { if (it.isNotEmpty()) model.select((it as List<Op<Boolean>>).compoundAnd()) else model.selectAll() }

        val orderBy = mutableListOf<Pair<Expression<*>, SortOrder>>()
        if (showOnTop != null && showOnTop == 1) {
            orderBy.add(PetHistories.showOnTop to SortOrder.DESC)
        }
        orderBy.add(PetHistories.id to SortOrder.DESC)

        return query
            .limit(limit, offset)
            .orderBy(*orderBy.toTypedArray())
            .map { rowToMap(it, columns, alias) }
    }

    fun createHistory(
        petId: Long,
        contentType: String,
        categoryId: Long? = null,
        content: String,
        showOnTop: Boolean = false,
        managerId: Long,
        creatorId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return PetHistories.insertAndGetId {
            it[this.petId] = petId
            it[this.contentType] = contentType
            it[this.categoryId] = categoryId
            it[this.content] = content
            it[this.showOnTop] = if (showOnTop) 1 else 0
            it[this.managerId] = managerId
            it[this.creatorId] = creatorId
            it[this.updaterId] = creatorId
        }.value
    }

    fun batchDeletePetHistory(targetIds: List<Long>, updaterId: String = Constants.SYSTEM_USERNAME): Int {
        return PetHistories.update({ PetHistories.id inList targetIds }) {
            it[this.deleted] = 1
            it[this.updaterId] = updaterId
        }
    }
}