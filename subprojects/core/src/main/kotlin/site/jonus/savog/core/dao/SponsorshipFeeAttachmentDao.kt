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
import site.jonus.savog.core.model.SponsorshipFeeAttachment
import site.jonus.savog.core.model.SponsorshipFeeAttachments

@Repository
@Transactional
class SponsorshipFeeAttachmentDao : BaseDao() {
    fun findAttachmentsBySponsorshipFeeId(sponsorshipFeeIds: List<Long>): List<SponsorshipFeeAttachment> {
        val query = SponsorshipFeeAttachments
            .select { SponsorshipFeeAttachments.sponsorshipFeeId inList sponsorshipFeeIds }
            .andWhere { SponsorshipFeeAttachments.deleted eq 0 }
            .orderBy(SponsorshipFeeAttachments.id to SortOrder.DESC)

        return SponsorshipFeeAttachment.wrapRows(query).toList()
    }

    fun getSponsorshipFeeAttachment(id: Long): SponsorshipFeeAttachment? {
        return SponsorshipFeeAttachment[id]
    }

    fun createSponsorshipFeeAttachment(
        sponsorshipFeeId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        creatorId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return SponsorshipFeeAttachments.insertAndGetId {
            it[this.sponsorshipFeeId] = sponsorshipFeeId
            it[this.type] = type
            if (bucket != null) it[this.bucket] = bucket
            if (key != null) it[this.key] = key
            it[this.filename] = filename
            it[this.creatorId] = creatorId
            it[this.updaterId] = creatorId
        }.value
    }

    fun updateSponsorshipFeeAttachment(
        id: Long,
        sponsorshipFeeId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Int {
        return SponsorshipFeeAttachments.update(
            {
                listOfNotNull(
                    SponsorshipFeeAttachments.id eq id,
                    SponsorshipFeeAttachments.sponsorshipFeeId eq sponsorshipFeeId
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

    fun upsertSponsorshipFeeAttachment(
        sponsorshipFeeId: Long,
        type: String,
        bucket: String? = null,
        key: String? = null,
        filename: String,
        id: Long? = null,
        updaterId: String = Constants.SYSTEM_USERNAME
    ): Long {
        return when (id?.let { getSponsorshipFeeAttachment(it) }) {
            null -> createSponsorshipFeeAttachment(sponsorshipFeeId, type, bucket, key, filename, updaterId)
            else -> updateSponsorshipFeeAttachment(id, sponsorshipFeeId, type, bucket, key, filename, updaterId).toLong()
        }
    }

    fun deleteSponsorshipFeeAttachment(ids: List<Long>, updaterId: String = Constants.SYSTEM_USERNAME): Int {
        return SponsorshipFeeAttachments.update({ (SponsorshipFeeAttachments.id inList ids) }) {
            it[this.deleted] = 1
            it[this.updaterId] = updaterId
        }
    }
}