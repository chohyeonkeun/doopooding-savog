package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.jonus.savog.core.Constants
import site.jonus.savog.core.model.SponsorshipFeeTransactionHistories
import site.jonus.savog.core.model.SponsorshipFeeTransactionHistory
import site.jonus.savog.core.model.TransactionHistoryAttachment
import site.jonus.savog.core.model.TransactionHistoryAttachments
import java.time.Instant

@Repository
@Transactional
class SponsorshipFeeTransactionHistoryDao : BaseDao() {
    fun count(
        sponsorshipFeeIds: List<Long>? = null,
        transactionType: String? = null,
        target: String? = null,
        transactionStDate: Instant? = null,
        transactionEdDate: Instant? = null,
        creatorId: String? = null
    ): Int {
        val conditions = listOfNotNull(
            sponsorshipFeeIds?.let { SponsorshipFeeTransactionHistories.sponsorshipFeeId inList it },
            transactionType?.let { SponsorshipFeeTransactionHistories.transactionType eq it },
            target?.let { SponsorshipFeeTransactionHistories.target eq it },
            transactionStDate?.let { SponsorshipFeeTransactionHistories.transactionDate greaterEq it },
            transactionEdDate?.let { SponsorshipFeeTransactionHistories.transactionDate lessEq it },
            creatorId?.let { SponsorshipFeeTransactionHistories.creatorId eq it }
        )
        val query = if (conditions.count() > 0) SponsorshipFeeTransactionHistories.select(conditions.compoundAnd()) else SponsorshipFeeTransactionHistories.selectAll()

        return query.count()
    }

    fun search(
        sponsorshipFeeIds: List<Long>? = null,
        transactionType: String? = null,
        target: String? = null,
        transactionStDate: Instant? = null,
        transactionEdDate: Instant? = null,
        creatorId: String? = null,
        limit: Int = Constants.Paging.DEFAULT_LIMIT,
        offset: Int = Constants.Paging.DEFAULT_OFFSET
    ): List<SponsorshipFeeTransactionHistory> {
        val conditions = listOfNotNull(
            sponsorshipFeeIds?.let { SponsorshipFeeTransactionHistories.sponsorshipFeeId inList it },
            transactionType?.let { SponsorshipFeeTransactionHistories.transactionType eq it },
            target?.let { SponsorshipFeeTransactionHistories.target eq it },
            transactionStDate?.let { SponsorshipFeeTransactionHistories.transactionDate greaterEq it },
            transactionEdDate?.let { SponsorshipFeeTransactionHistories.transactionDate lessEq it },
            creatorId?.let { SponsorshipFeeTransactionHistories.creatorId eq it },
            SponsorshipFeeTransactionHistories.deleted eq 0
        )
        val query = if (conditions.count() > 0) SponsorshipFeeTransactionHistories.select(conditions.compoundAnd()) else SponsorshipFeeTransactionHistories.selectAll()

        return SponsorshipFeeTransactionHistory.wrapRows(query.limit(limit, offset)).toList()
    }

    fun findAttachmentsByIds(ids: List<Long>): List<TransactionHistoryAttachment> {
        val query = TransactionHistoryAttachments
            .select { TransactionHistoryAttachments.sponsorshipFeeTransactionHistoryId inList ids }
            .andWhere { TransactionHistoryAttachments.deleted eq 0 }

        return TransactionHistoryAttachment.wrapRows(query).toList()
    }

    fun create(
        sponsorshipFeeId: Long,
        transactionType: String,
        amount: Int,
        target: String,
        transactionDate: Instant,
        creatorId: String
    ): Long {
        return SponsorshipFeeTransactionHistories.insertAndGetId {
            it[this.sponsorshipFeeId] = sponsorshipFeeId
            it[this.transactionType] = transactionType
            it[this.amount] = amount
            it[this.target] = target
            it[this.transactionDate] = transactionDate
            it[this.creatorId] = creatorId
            it[this.updaterId] = creatorId
        }.value
    }

    fun update(
        id: Long,
        transactionType: String? = null,
        amount: Int? = null,
        target: String? = null,
        transactionDate: Instant? = null,
        deleted: Boolean? = null,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return SponsorshipFeeTransactionHistories.update({ SponsorshipFeeTransactionHistories.id eq id }) { stmt ->
            transactionType?.let { stmt[this.transactionType] = it }
            amount?.let { stmt[this.amount] = it }
            target?.let { stmt[this.target] = it }
            transactionDate?.let { stmt[this.transactionDate] = it }
            deleted?.let { stmt[this.deleted] = if (it) 1 else 0 }
            stmt[this.updaterId] = updaterId
        }
    }

    fun batchDelete(targetIds: List<Long>, updaterId: String = Constants.SYSTEM_USERNAME): Int {
        return SponsorshipFeeTransactionHistories.update({ SponsorshipFeeTransactionHistories.id inList targetIds }) {
            it[this.deleted] = 1
            it[this.updaterId] = updaterId
        }
    }
}