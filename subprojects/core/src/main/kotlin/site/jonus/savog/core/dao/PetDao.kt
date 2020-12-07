package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
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
import site.jonus.savog.core.model.Pet
import site.jonus.savog.core.model.PetHistories
import site.jonus.savog.core.model.Pets
import site.jonus.savog.core.model.Users
import java.time.LocalDate

@Repository
@Transactional
class PetDao : BaseDao() {
    fun create(
        type: String,
        name: String,
        breeds: String,
        gender: String,
        weight: Int,
        adoptionStatus: String,
        birthDate: LocalDate,
        creatorId: String
    ): Long {
        return Pets.insertAndGetId { stmt ->
            stmt[this.type] = type
            stmt[this.name] = name
            stmt[this.breeds] = breeds
            stmt[this.gender] = gender
            stmt[this.weight] = weight
            stmt[this.adoptionStatus] = adoptionStatus
            stmt[this.birthDate] = birthDate
            stmt[this.creatorId] = creatorId
            stmt[this.updaterId] = creatorId
        }.value
    }

    fun count(
        ids: List<Long>? = null,
        type: String? = null,
        name: String? = null,
        breeds: String? = null,
        gender: String? = null,
        adoptionStatus: String? = null,
        birthStDate: LocalDate? = null,
        birthEdDate: LocalDate? = null,
        creatorId: String? = null,
        updaterId: String? = null
    ): Int {
        val conditions = listOfNotNull(
            ids?.let { Pets.id inList it },
            type?.let { Pets.type eq it },
            name?.let { Pets.name eq it },
            breeds?.let { Pets.breeds eq it },
            gender?.let { Pets.gender eq it },
            adoptionStatus?.let { Pets.adoptionStatus eq it },
            birthStDate?.let { Pets.birthDate greaterEq it },
            birthEdDate?.let { Pets.birthDate lessEq it },
            creatorId?.let { Pets.creatorId eq it },
            updaterId?.let { Pets.updaterId eq it }
        )
        val query = conditions.let { if (it.count() > 0) Pets.select(it.compoundAnd()) else Pets.selectAll() }

        return query.count()
    }

    fun search(
        ids: List<Long>? = null,
        type: String? = null,
        name: String? = null,
        breeds: String? = null,
        gender: String? = null,
        adoptionStatus: String? = null,
        birthStDate: LocalDate? = null,
        birthEdDate: LocalDate? = null,
        creatorId: String? = null,
        updaterId: String? = null,
        limit: Int = Constants.Paging.DEFAULT_LIMIT,
        offset: Int = Constants.Paging.DEFAULT_OFFSET
    ): List<Pet?> {
        val model = Pets.slice(Pets.columns)
        val conditions = listOfNotNull(
            ids?.let { Pets.id inList it },
            type?.let { Pets.type eq it },
            name?.let { Pets.name eq it },
            breeds?.let { Pets.breeds eq it },
            gender?.let { Pets.gender eq it },
            adoptionStatus?.let { Pets.adoptionStatus eq it },
            birthStDate?.let { Pets.birthDate greaterEq it },
            birthEdDate?.let { Pets.birthDate lessEq it },
            creatorId?.let { Pets.creatorId eq it },
            updaterId?.let { Pets.updaterId eq it }
        )
        val query = conditions.let { if (it.count() > 0) model.select(it.compoundAnd()) else model.selectAll() }

        return query
            .limit(limit, offset)
            .orderBy(Pets.id to SortOrder.DESC)
            .map { Pet.wrapRow(it) }
    }

    fun update(
        petId: Long,
        type: String? = null,
        name: String? = null,
        breeds: String? = null,
        weight: Int? = null,
        adoptionStatus: String? = null,
        birthDate: LocalDate? = null,
        deleted: Int? = null,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return Pets.update({ Pets.id eq petId }) { stmt ->
            type?.let { stmt[this.type] = it }
            name?.let { stmt[this.name] = it }
            breeds?.let { stmt[this.breeds] = it }
            weight?.let { stmt[this.weight] = it }
            adoptionStatus?.let { stmt[this.adoptionStatus] = it }
            birthDate?.let { stmt[this.birthDate] = it }
            deleted?.let { stmt[this.deleted] = it }
            stmt[this.updaterId] = updaterId
        }
    }

    fun findById(id: Long): Pet {
        return Pet[id]
    }

    fun countHistories(
        petIds: List<Long>? = null,
        managerId: Long? = null,
        contentType: String? = null,
        content: String? = null,
        showOnTop: Int? = null,
        deleted: Int? = null
    ): Int {
        val model = PetHistories
            .join(Managers, JoinType.LEFT, PetHistories.managerId, Managers.id)
            .join(Users, JoinType.LEFT, Managers.userId, Users.id)
            .join(Categories, JoinType.LEFT, PetHistories.categoryId, Categories.id)
        val conditions = listOfNotNull(
            petIds?.let { PetHistories.petId inList it },
            managerId?.let { Managers.id eq it },
            contentType?.let { PetHistories.contentType eq it },
            content?.let { PetHistories.content eq it },
            showOnTop?.let { PetHistories.showOnTop eq it },
            deleted?.let { PetHistories.deleted eq it }
        )
        val query = conditions.let { if (it.count() > 0) model.select(it.compoundAnd()) else model.selectAll() }
        return query.count()
    }

    fun searchHistories(
        petIds: List<Long>? = null,
        managerId: Long? = null,
        contentType: String? = null,
        content: String? = null,
        showOnTop: Int? = null,
        deleted: Int? = null,
        limit: Int = Constants.Paging.DEFAULT_LIMIT,
        offset: Int = Constants.Paging.DEFAULT_OFFSET
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
        val conditions = listOfNotNull(
            petIds?.let { PetHistories.petId inList it },
            managerId?.let { Managers.id eq it },
            contentType?.let { PetHistories.contentType eq it },
            content?.let { PetHistories.content eq it },
            showOnTop?.let { PetHistories.showOnTop eq it },
            deleted?.let { PetHistories.deleted eq it }
        )
        val query = conditions.let { if (it.count() > 0) model.select(it.compoundAnd()) else model.selectAll() }

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