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
import site.jonus.savog.core.model.TransactionHistoryAttachment
import site.jonus.savog.core.model.TransactionHistoryAttachments

@Repository
@Transactional
class TransactionHistoryAttachmentDao : BaseDao() {
    fun findAttachmentsByTransactionHistoryIds(transactionHistoryIds: List<Long>): List<TransactionHistoryAttachment> {
        val query = TransactionHistoryAttachments
            .select { TransactionHistoryAttachments.sponsorshipFeeTransactionHistoryId inList transactionHistoryIds }
            .andWhere { TransactionHistoryAttachments.deleted eq 0 }
            .orderBy(TransactionHistoryAttachments.id to SortOrder.DESC)

        return TransactionHistoryAttachment.wrapRows(query).toList()
    }

    fun getTransactionHistoryAttachment(id: Long): TransactionHistoryAttachment? {
        return TransactionHistoryAttachment[id]
    }

    fun createTransactionHistoryAttachment(
        transactionHistoryId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        creatorId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return TransactionHistoryAttachments.insertAndGetId {
            it[this.sponsorshipFeeTransactionHistoryId] = transactionHistoryId
            it[this.type] = type
            if (bucket != null) it[this.bucket] = bucket
            if (key != null) it[this.key] = key
            it[this.filename] = filename
            it[this.creatorId] = creatorId
            it[this.updaterId] = creatorId
        }.value
    }

    fun updateTransactionHistoryAttachment(
        id: Long,
        transactionHistoryId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return TransactionHistoryAttachments.update(
            {
                listOfNotNull(
                    TransactionHistoryAttachments.id eq id,
                    TransactionHistoryAttachments.sponsorshipFeeTransactionHistoryId eq transactionHistoryId
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

    fun upsertTransactionHistoryAttachment(
        transactionHistoryId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        id: Long? = null,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return when (id?.let { getTransactionHistoryAttachment(it) }) {
            null -> createTransactionHistoryAttachment(transactionHistoryId, type, bucket, key, filename, updaterId)
            else -> updateTransactionHistoryAttachment(id, transactionHistoryId, type, bucket, key, filename, updaterId).toLong()
        }
    }

    fun deleteTransactionHistoryAttachment(ids: List<Long>, updaterId: String = Constants.SYSTEM_USERNAME): Int {
        return TransactionHistoryAttachments.update({ (TransactionHistoryAttachments.id inList ids) }) {
            it[this.deleted] = 1
            it[this.updaterId] = updaterId
        }
    }
}