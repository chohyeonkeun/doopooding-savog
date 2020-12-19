package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.jonus.savog.core.Constants
import site.jonus.savog.core.model.PetComment
import site.jonus.savog.core.model.PetComments

@Repository
@Transactional
class PetCommentDao : BaseDao() {
    fun countPetCommentsByPetId(petId: Long): Int {
        val query = PetComments
            .select { PetComments.petId eq petId }
            .andWhere { PetComments.deleted eq 0 }
        return query.count()
    }

    fun findPetCommentsByPetId(
        petId: Long,
        limit: Int = Constants.Paging.DEFAULT_LIMIT,
        offset: Int = Constants.Paging.DEFAULT_OFFSET
    ): List<PetComment> {
        val query = PetComments
            .select { PetComments.petId eq petId }
            .andWhere { PetComments.deleted eq 0 }
        return query
            .limit(limit, offset)
            .orderBy(PetComments.createdAt to SortOrder.DESC)
            .map {
                PetComment.wrapRow(it)
            }
    }

    fun create(
        petId: Long,
        userId: Long,
        parentId: Long? = null,
        comment: String,
        showOnTop: Boolean = false
    ): Long {
        return PetComments.insertAndGetId { stmt ->
            stmt[this.petId] = petId
            stmt[this.userId] = userId
            parentId?.let { stmt[this.parentId] = it }
            stmt[this.comment] = comment
            stmt[this.showOnTop] = if (showOnTop) 1 else 0
        }.value
    }

    fun update(
        commentId: Long,
        comment: String? = null,
        showOnTop: Boolean? = null,
        deleted: Boolean? = null
    ): Int {
        return PetComments.update({ PetComments.id eq commentId }) { stmt ->
            comment?.let { stmt[this.comment] = it }
            showOnTop?.let { stmt[this.showOnTop] = if (it) 1 else 0 }
            deleted?.let { stmt[this.deleted] = if (it) 1 else 0 }
        }
    }
}
