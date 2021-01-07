package site.jonus.savog.api.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.jonus.savog.api.dto.SponsorshipFeeDto
import site.jonus.savog.api.dto.SponsorshipFeeHistoryDto
import site.jonus.savog.api.dto.SponsorshipFeeHistoryInfo
import site.jonus.savog.api.dto.SponsorshipFeeInfo
import site.jonus.savog.api.dto.SponsorshipFeeTransactionHistoryDto
import site.jonus.savog.api.dto.TransactionHistoryInfo
import site.jonus.savog.core.Codes
import site.jonus.savog.core.Constants
import site.jonus.savog.core.dao.PetAttachmentDao
import site.jonus.savog.core.dao.SponsorshipFeeDao
import site.jonus.savog.core.dao.SponsorshipFeeTransactionHistoryDao
import site.jonus.savog.core.dao.TransactionHistoryAttachmentDao
import site.jonus.savog.core.service.FileUploadService
import site.jonus.savog.core.util.History
import site.jonus.savog.core.util.Times
import java.time.Instant
import java.time.format.DateTimeFormatter

@Service
class SponsorshipFeeService(
    private val sponsorshipFeeDao: SponsorshipFeeDao,
    private val transactionHistoryDao: SponsorshipFeeTransactionHistoryDao,
    private val attachmentDao: TransactionHistoryAttachmentDao,
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
            val petAttachments = petAttachmentDao.findPetAttachmentByPetIds(sponsorshipFees.map { it.petId })
            val petAttachmentsGroupById = if (petAttachments.isNotEmpty()) petAttachments.groupBy { it!!.petId } else null

            return SponsorshipFeeDto(
                total = total,
                sponsorshipFees = sponsorshipFees.map { fee ->
                    SponsorshipFeeInfo(
                        id = fee.id.value,
                        petId = fee.petId,
                        targetAmount = fee.targetAmount,
                        status = fee.status,
                        petAttachmentUrls = petAttachmentsGroupById?.let { attachments ->
                            attachments[fee.petId]?.map {
                                fileUploadService.getSignedUrl(it!!.bucket, it.key, it.filename).toString()
                            }
                        } ?: listOf(),
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

    fun getTransactionHistories(
        sponsorshipFeeIds: List<Long>?,
        transactionType: String?,
        target: String?,
        transactionStDate: Long?,
        transactionEdDate: Long?,
        creatorId: String?,
        limit: Int?,
        offset: Int?
    ): SponsorshipFeeTransactionHistoryDto {
        val transactionStDate = transactionStDate?.let { Instant.ofEpochMilli(it) }
        val transactionEdDate = transactionEdDate?.let { Instant.ofEpochMilli(it) }

        val total = transactionHistoryDao.count(
            sponsorshipFeeIds = sponsorshipFeeIds,
            transactionType = transactionType,
            target = target,
            transactionStDate = transactionStDate,
            transactionEdDate = transactionEdDate,
            creatorId = creatorId
        )
        val transactionHistories = transactionHistoryDao.search(
            sponsorshipFeeIds = sponsorshipFeeIds,
            transactionType = transactionType,
            target = target,
            transactionStDate = transactionStDate,
            transactionEdDate = transactionEdDate,
            creatorId = creatorId,
            limit = limit?.let { it } ?: Constants.Paging.DEFAULT_LIMIT,
            offset = offset?.let { it } ?: Constants.Paging.DEFAULT_OFFSET
        )
        val attachments = transactionHistoryDao.findAttachmentsByIds(transactionHistories.map { it.id.value }).groupBy { it.sponsorshipFeeTransactionHistoryId }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(Times.KST)
        try {
            return SponsorshipFeeTransactionHistoryDto(
                total = total,
                transactionHistories = transactionHistories.map { transaction ->
                    TransactionHistoryInfo(
                        id = transaction.id.value,
                        sponsorshipFeeId = transaction.sponsorshipFeeId,
                        transactionType = transaction.transactionType,
                        amount = transaction.amount,
                        target = transaction.target,
                        transactionDate = formatter.format(transaction.transactionDate),
                        urls = attachments[transaction.id.value]?.map {
                            fileUploadService.getSignedUrl(it.bucket, it.key, it.filename).toString()
                        },
                        creatorId = transaction.creatorId,
                        updaterId = transaction.updaterId,
                        createdAt = formatter.format(transaction.createdAt),
                        updatedAt = formatter.format(transaction.updatedAt)
                    )
                }
            )
        } catch (e: Exception) {
            logger.warn("get sponsorship fee transaction histories fail", e)
            throw e
        }
    }

    fun createSponsorshipFee(params: Map<String, Any>): Long {
        val petId = params["petId"].toString().toLong()
        val targetAmount = params["targetAmount"].toString().toInt()
        val status = params["status"].toString()
        val requesterId = params["requesterId"].toString().toLong()
        val requesterUsername = params["requesterUsername"].toString()

        try {
            val sponsorshipFeeId = sponsorshipFeeDao.create(
                petId = petId,
                targetAmount = targetAmount,
                status = status,
                creatorId = requesterUsername
            )

            sponsorshipFeeDao.createHistory(
                sponsorshipFeeId = sponsorshipFeeId,
                contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                content = "sponsorship fee create - sponsorshipFeeId: $sponsorshipFeeId",
                managerId = requesterId,
                creatorId = requesterUsername
            )

            return sponsorshipFeeId
        } catch (e: Exception) {
            logger.warn("create sponsorship fee fail", e)
            throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun createTransactionHistory(params: Map<String, Any>): Long {
        val attachments = params["attachments"]?.let { it as List<Map<String, Any>> }
        val sponsorshipFeeId = params["sponsorshipFeeId"].toString().toLong()
        val transactionType = params["transactionType"].toString()
        val amount = params["amount"].toString().toInt()
        val target = params["target"].toString()
        val transactionDate = Instant.ofEpochMilli(params["transactionDate"].toString().toLong())
        val requesterUsername = params["requesterUsername"].toString()

        try {
            val transactionHistoryId = transactionHistoryDao.create(
                sponsorshipFeeId = sponsorshipFeeId,
                transactionType = transactionType,
                amount = amount,
                target = target,
                transactionDate = transactionDate,
                creatorId = requesterUsername
            )

            attachments?.map { attachment ->
                val newObject = fileUploadService.preserve("transaction-history-attachment", attachment["bucket"].toString(), attachment["key"].toString())
                attachmentDao.upsertTransactionHistoryAttachment(
                    transactionHistoryId = transactionHistoryId,
                    type = attachment["type"].toString(),
                    bucket = newObject.bucket,
                    key = newObject.key,
                    filename = attachment["filename"].toString(),
                    updaterId = requesterUsername
                )
            }

            return transactionHistoryId
        } catch (e: Exception) {
            logger.warn("create sponsorship fee transaction history fail", e)
            throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun updateSponsorshipFee(params: Map<String, Any>): Boolean {
        val id = params["id"].toString().toLong()
        val targetAmount = params["targetAmount"]?.toString()?.toInt()
        val status = params["status"]?.toString()
        val deleted = params["deleted"]?.let { it as Boolean }
        val requesterId = params["requesterId"].toString().toLong()
        val requesterUsername = params["requesterUsername"].toString()
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
                    updaterId = requesterUsername
                )

                sponsorshipFeeDao.createHistory(
                    sponsorshipFeeId = id,
                    contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                    content = historyContents.joinToString("\n"),
                    managerId = requesterId,
                    creatorId = requesterUsername
                )
            }

            return true
        } catch (e: Exception) {
            logger.warn("update sponsorship fee fail", e)
            throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun updateTransactionHistory(params: Map<String, Any>): Boolean {
        val id = params["id"].toString().toLong()
        val attachments = params["attachments"]?.let { it as List<Map<String, Any>> }
        val clearAttachments = params["clearAttachments"]?.let { it as Boolean }
        val transactionType = params["transactionType"]?.toString()
        val amount = params["amount"]?.toString()?.toInt()
        val target = params["target"]?.toString()
        val transactionDate = params["transactionDate"]?.let { Instant.ofEpochMilli(it.toString().toLong()) }
        val deleted = params["deleted"]?.let { it as Boolean }
        val requesterUsername = params["requesterUsername"].toString()

        try {
            transactionHistoryDao.update(
                id = id,
                transactionType = transactionType,
                amount = amount,
                target = target,
                transactionDate = transactionDate,
                deleted = deleted,
                updaterId = requesterUsername
            )

            val prevAttachments = attachmentDao.findAttachmentsByTransactionHistoryIds(listOf(id))
            attachments?.let {
                val attachmentIds = attachments?.mapNotNull { attachment ->
                    val attachmentId = attachment["attachmentId"]?.toString()?.toLong()
                    val newObject = attachmentId?.let {
                        fileUploadService.preserve("transaction-history-attachment", attachment["bucket"].toString(), attachment["key"].toString())
                    }
                    if (attachment.count() > 0) {
                        attachmentDao.upsertTransactionHistoryAttachment(
                            transactionHistoryId = id,
                            type = attachment["type"].toString(),
                            bucket = newObject?.bucket,
                            key = newObject?.key,
                            filename = attachment["filename"].toString(),
                            id = attachmentId,
                            updaterId = requesterUsername
                        )
                    }
                    attachmentId
                }

                // 삭제 요청된 이전 첨부파일 삭제
                val prevAttachmentIdsToDelete = prevAttachments?.mapNotNull {
                    if (!attachmentIds.contains(it.id.value)) {
                        it.id.value
                    } else null
                }
                if (prevAttachmentIdsToDelete.count() > 0) {
                    attachmentDao.deleteTransactionHistoryAttachment(prevAttachmentIdsToDelete, updaterId = requesterUsername)
                }
            }

            if (clearAttachments !== null && clearAttachments) {
                attachmentDao.deleteTransactionHistoryAttachment(prevAttachments.map { it.id.value }, updaterId = requesterUsername)
            }

            return true
        } catch (e: Exception) {
            logger.warn("update sponsorship fee transaction history fail", e)
            throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deleteSponsorshipFee(deleteParams: Map<String, Any>): Boolean {
        val targetIds = deleteParams["targetIds"] as List<Long>
        val requesterId = deleteParams["requesterId"].toString().toLong()
        val requesterUsername = deleteParams["requesterUsername"].toString()

        try {
            sponsorshipFeeDao.batchDelete(targetIds, requesterUsername)

            targetIds.map { id ->
                sponsorshipFeeDao.createHistory(
                    sponsorshipFeeId = id,
                    contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                    content = "후원금 데이터 삭제",
                    managerId = requesterId,
                    creatorId = requesterUsername
                )
            }
            return true
        } catch (e: Exception) {
            logger.warn("batch delete sponsorship fee fail", e)
            throw e
        }
    }
}