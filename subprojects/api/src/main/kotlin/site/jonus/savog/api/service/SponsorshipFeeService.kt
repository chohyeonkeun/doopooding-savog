package site.jonus.savog.api.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.jonus.savog.api.dto.SponsorshipFeeDto
import site.jonus.savog.api.dto.SponsorshipFeeHistoryDto
import site.jonus.savog.api.dto.SponsorshipFeeHistoryInfo
import site.jonus.savog.api.dto.SponsorshipFeeInfo
import site.jonus.savog.core.Codes
import site.jonus.savog.core.Constants
import site.jonus.savog.core.dao.PetAttachmentDao
import site.jonus.savog.core.dao.SponsorshipFeeDao
import site.jonus.savog.core.service.FileUploadService
import site.jonus.savog.core.util.History
import java.time.Instant

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
                        petAttachmentUrls = (petAttachments[fee.petId] ?: error("")).map {
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

    fun getSponsorshipFeeHistories(
        sponsorshipFeeIds: List<Long>?,
        managerId: Long?,
        contentType: String?,
        content: String?,
        showOnTop: Int?,
        deleted: Int?,
        limit: Int?,
        offset: Int?
    ): SponsorshipFeeHistoryDto {
        try {
            val total = sponsorshipFeeDao.countHistories(
                sponsorshipFeeIds = sponsorshipFeeIds,
                managerId = managerId,
                contentType = contentType,
                content = content,
                showOnTop = showOnTop,
                deleted = deleted
            )
            val sponsorshipFeeHistories = sponsorshipFeeDao.searchHistories(
                sponsorshipFeeIds = sponsorshipFeeIds,
                managerId = managerId,
                contentType = contentType,
                content = content,
                showOnTop = showOnTop,
                deleted = deleted,
                limit = limit?.let { it } ?: Constants.Paging.DEFAULT_LIMIT,
                offset = offset?.let { it } ?: Constants.Paging.DEFAULT_OFFSET
            )
            return SponsorshipFeeHistoryDto(
                total = total,
                histories = sponsorshipFeeHistories.map {
                    SponsorshipFeeHistoryInfo(
                        id = it["id"].toString().toLong(),
                        sponsorshipFeeId = it["sponsorshipFeeId"].toString().toLong(),
                        categoryId = it["categoryId"]?.toString()?.toLong(),
                        categoryName = it["categoryName"]?.toString(),
                        managerId = it["managerId"].toString().toLong(),
                        managerName = it["managerName"].toString(),
                        contentType = it["contentType"].toString(),
                        content = it["content"]?.toString(),
                        creatorId = it["creatorId"].toString(),
                        updaterId = it["updaterId"].toString(),
                        showOnTop = it["showOnTop"].toString().toInt() == 1,
                        deleted = it["deleted"].toString().toInt() == 1,
                        createdAt = (it["createdAt"] as Instant).toEpochMilli(),
                        updatedAt = (it["updatedAt"] as Instant).toEpochMilli()
                    )
                }
            )
        } catch (e: Exception) {
            logger.warn("get sponsorship fee histories fail", e)
            throw e
        }
    }

    fun createSponsorshipFee(params: Map<String, Any>): Long {
        val petId = params["petId"].toString().toLong()
        val targetAmount = params["targetAmount"].toString().toInt()
        val status = params["status"].toString()
        val managerId = params["managerId"].toString().toLong()
        val creatorId = params["creatorId"].toString()

        try {
            val sponsorshipFeeId = sponsorshipFeeDao.create(
                petId = petId,
                targetAmount = targetAmount,
                status = status,
                creatorId = creatorId
            )

            sponsorshipFeeDao.createHistory(
                sponsorshipFeeId = sponsorshipFeeId,
                contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                content = "pet create - petId: $petId",
                managerId = managerId,
                creatorId = creatorId
            )

            return sponsorshipFeeId
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
        val managerId = params["managerId"].toString().toLong()
        val updaterId = params["updaterId"].toString()
        val historyContents = mutableListOf<String>()

        try {
            if (listOfNotNull(targetAmount, status, deleted).count() > 0) {
                val originMap = sponsorshipFeeDao.findSponsorshipFeeToMap(id)
                val updateMap = mutableMapOf(
                    "targetAmount" to targetAmount,
                    "status" to status,
                    "deleted" to deleted
                )
                historyContents.add(History.getContent(originMap, updateMap, "후원금 정보 변경"))

                sponsorshipFeeDao.update(
                    id = id,
                    targetAmount = targetAmount,
                    status = status,
                    deleted = deleted,
                    updaterId = updaterId
                )

                sponsorshipFeeDao.createHistory(
                    sponsorshipFeeId = id,
                    contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                    content = historyContents.joinToString("\n"),
                    managerId = managerId,
                    creatorId = updaterId
                )
            }

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