package site.jonus.savog.api.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.jonus.savog.api.dto.SponsorshipFeeDto
import site.jonus.savog.api.dto.SponsorshipFeeInfo
import site.jonus.savog.core.Constants
import site.jonus.savog.core.dao.PetAttachmentDao
import site.jonus.savog.core.dao.SponsorshipFeeDao
import site.jonus.savog.core.service.FileUploadService

@Service
class SponsorshipFeeService(
    private val sponsorshipFeeDao: SponsorshipFeeDao,
    private val petAttachmentDao: PetAttachmentDao,
    private val fileUploadService: FileUploadService
) : BaseService() {
    private val logger = LoggerFactory.getLogger(SponsorshipFeeService::class.java)
    fun getSponsorshipFees(
        ids: List<Long>? = null,
        petIds: List<Long>? = null,
        status: String? = null,
        creatorId: String? = null,
        limit: Int?,
        offset: Int?
    ): SponsorshipFeeDto {
        try {
            val total = sponsorshipFeeDao.count(
                ids = ids,
                petIds = petIds,
                status = status,
                creatorId = creatorId
            )
            val sponsorshipFees = sponsorshipFeeDao.search(
                ids = ids,
                petIds = petIds,
                status = status,
                creatorId = creatorId,
                limit = limit?.let { it } ?: Constants.Paging.DEFAULT_LIMIT,
                offset = offset?.let { it } ?: Constants.Paging.DEFAULT_OFFSET
            )
            val petAttachments = petAttachmentDao.findPetAttachmentByPetIds(sponsorshipFees.map { it.petId }).groupBy { it.petId }

            return SponsorshipFeeDto(
                total = total,
                sponsorshipFees = sponsorshipFees.map { fee ->
                    SponsorshipFeeInfo(
                        id = fee.id.value,
                        petId = fee.petId,
                        targetAmount = fee.targetAmount,
                        status = fee.status,
                        petAttachmentUrl = (petAttachments[fee.petId] ?: error("")).map {
                            fileUploadService.getSignedUrl(it.bucket, it.key, it.filename).toString()
                        },
                        creatorId = fee.creatorId,
                        updaterId = fee.updaterId,
                        createdAt = fee.createdAt.toEpochMilli(),
                        updatedAt = fee.updatedAt.toEpochMilli()
                    )
                }
            )
        } catch (e: Exception) {
            logger.warn("get sponsorship fee fail", e)
            throw e
        }
    }

    fun createSponsorshipFee(params: Map<String, Any>): Long {
        val petId = params["petId"].toString().toLong()
        val targetAmount = params["targetAmount"].toString().toInt()
        val status = params["status"].toString()
        val creatorId = params["creatorId"].toString()

        try {
            return sponsorshipFeeDao.create(
                petId = petId,
                targetAmount = targetAmount,
                status = status,
                creatorId = creatorId
            )
        } catch (e: Exception) {
            logger.warn("create sponsorship fee fail", e)
            throw e
        }
    }

    fun updateSponsorshipFee(params: Map<String, Any>): Boolean {
        val id = params["id"].toString().toLong()
        val targetAmount = params["targetAmount"]?.toString()?.toInt()
        val status = params["status"]?.toString()
        val deleted = params["deleted"]?.let { it as Boolean }
        val updaterId = params["updaterId"].toString()

        try {
            sponsorshipFeeDao.update(
                id = id,
                targetAmount = targetAmount,
                status = status,
                deleted = deleted,
                updaterId = updaterId
            )
            return true
        } catch (e: Exception) {
            logger.warn("update sponsorship fee fail", e)
            throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deleteSponsorshipFee(deleteParams: Map<String, Any>): Boolean {
        val targetIds = deleteParams["targetIds"] as List<Long>
        val updaterId = deleteParams["updaterId"].toString()

        try {
            sponsorshipFeeDao.batchDelete(targetIds, updaterId)
            return true
        } catch (e: Exception) {
            logger.warn("batch delete sponsorship fee fail", e)
            throw e
        }
    }
}