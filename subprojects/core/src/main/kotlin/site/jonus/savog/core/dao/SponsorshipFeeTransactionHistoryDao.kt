package site.jonus.savog.core.dao

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
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
import java.time.Instant

@Repository
@Transactional
class SponsorshipFeeTransactionHistoryDao : BaseDao() {
    fun countHistories(
        transactionType: String? = null,
        target: String? = null,
        transactionStDate: Instant? = null,
        transactionEdDate: Instant? = null,
        creatorId: String? = null
    ): Int {
        val conditions = listOfNotNull(
            transactionType?.let { SponsorshipFeeTransactionHistories.transactionType eq it },
            target?.let { SponsorshipFeeTransactionHistories.target eq it },
            transactionStDate?.let { SponsorshipFeeTransactionHistories.transactionDate greaterEq it },
            transactionEdDate?.let { SponsorshipFeeTransactionHistories.transactionDate lessEq it },
            creatorId?.let { SponsorshipFeeTransactionHistories.creatorId eq it }
        )
        val query = if (conditions.count() > 0) SponsorshipFeeTransactionHistories.select(conditions.compoundAnd()) else SponsorshipFeeTransactionHistories.selectAll()

        return query.count()
    }

    fun searchHistories(
        transactionType: String? = null,
        target: String? = null,
        transactionStDate: Instant? = null,
        transactionEdDate: Instant? = null,
        creatorId: String? = null
    ): List<SponsorshipFeeTransactionHistory> {
        val conditions = listOfNotNull(
            transactionType?.let { SponsorshipFeeTransactionHistories.transactionType eq it },
            target?.let { SponsorshipFeeTransactionHistories.target eq it },
            transactionStDate?.let { SponsorshipFeeTransactionHistories.transactionDate greaterEq it },
            transactionEdDate?.let { SponsorshipFeeTransactionHistories.transactionDate lessEq it },
            creatorId?.let { SponsorshipFeeTransactionHistories.creatorId eq it },
            SponsorshipFeeTransactionHistories.deleted eq 0
        )
        val query = if (conditions.count() > 0) SponsorshipFeeTransactionHistories.select(conditions.compoundAnd()) else SponsorshipFeeTransactionHistories.selectAll()

        return SponsorshipFeeTransactionHistory.wrapRows(query).toList()
    }

    fun createHistory(
        sponsorshipFeeId: Long,
        transactionType: String,
        amount: Int,
        target: String,
        transactionDate: Instant,
        creatorId: String,
        updaterId: String
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

    fun updateHistory(
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

    fun batchDeleteSponsorshipFeeTransactionHistory(targetIds: List<Long>, updaterId: String = Constants.SYSTEM_USERNAME): Int {
        return SponsorshipFeeTransactionHistories.update({ SponsorshipFeeTransactionHistories.id inList targetIds }) {
            it[this.deleted] = 1
            it[this.updaterId] = updaterId
        }
    }
}