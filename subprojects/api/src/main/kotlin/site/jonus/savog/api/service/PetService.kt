package site.jonus.savog.api.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.jonus.savog.api.dto.PetCommentDto
import site.jonus.savog.api.dto.PetCommentInfo
import site.jonus.savog.api.dto.PetDetailDto
import site.jonus.savog.api.dto.PetDiseaseDto
import site.jonus.savog.api.dto.PetDiseaseInfo
import site.jonus.savog.api.dto.PetDto
import site.jonus.savog.api.dto.PetHistoryDto
import site.jonus.savog.api.dto.PetHistoryInfo
import site.jonus.savog.api.dto.PetInfo
import site.jonus.savog.api.dto.PetTreatmentHistoryDto
import site.jonus.savog.api.dto.PetTreatmentHistoryInfo
import site.jonus.savog.core.Codes
import site.jonus.savog.core.Constants
import site.jonus.savog.core.dao.PetAttachmentDao
import site.jonus.savog.core.dao.PetCommentDao
import site.jonus.savog.core.dao.PetDao
import site.jonus.savog.core.dao.PetDiseaseDao
import site.jonus.savog.core.dao.PetTreatmentHistoryDao
import site.jonus.savog.core.service.FileUploadService
import site.jonus.savog.core.util.History
import site.jonus.savog.core.util.Times
import java.time.Instant

@Service
class PetService(
    private val petDao: PetDao,
    private val petCommentDao: PetCommentDao,
    private val petDiseaseDao: PetDiseaseDao,
    private val petTreatmentHistoryDao: PetTreatmentHistoryDao,
    private val attachmentDao: PetAttachmentDao,
    private val fileUploadService: FileUploadService
) : BaseService() {
    private val logger = LoggerFactory.getLogger(PetService::class.simpleName)
    fun getPets(
        ids: List<Long>? = null,
        type: String? = null,
        name: String? = null,
        breeds: String? = null,
        gender: String? = null,
        adoptionStatus: String? = null,
        birthStDate: Long? = null,
        birthEdDate: Long? = null,
        limit: Int?,
        offset: Int?
    ): PetDto {
        try {
            val total = petDao.count(
                ids = ids,
                type = type,
                name = name,
                breeds = breeds,
                gender = gender,
                adoptionStatus = adoptionStatus,
                birthStDate = birthStDate?.let { Instant.ofEpochMilli(it).atZone(Times.KST).toLocalDate() },
                birthEdDate = birthEdDate?.let { Instant.ofEpochMilli(it).atZone(Times.KST).toLocalDate() }
            )
            val pets = petDao.search(
                ids = ids,
                type = type,
                name = name,
                breeds = breeds,
                gender = gender,
                adoptionStatus = adoptionStatus,
                birthStDate = birthStDate?.let { Instant.ofEpochMilli(it).atZone(Times.KST).toLocalDate() },
                birthEdDate = birthEdDate?.let { Instant.ofEpochMilli(it).atZone(Times.KST).toLocalDate() },
                limit = limit?.let { it } ?: Constants.Paging.DEFAULT_LIMIT,
                offset = offset?.let { it } ?: Constants.Paging.DEFAULT_OFFSET
            )
            val petAttachments = attachmentDao.findPetAttachmentByPetIds(pets.map { it.id.value })
            val petAttachmentsGroupbyId = if (petAttachments.isNotEmpty()) petAttachments.groupBy { it!!.petId } else null

            return PetDto(
                total = total,
                pets = pets.map { pet ->
                    PetInfo(
                        id = pet.id.value,
                        type = pet.type,
                        name = pet.name,
                        breeds = pet.breeds,
                        gender = pet.gender,
                        adoptionStatus = pet.adoptionStatus,
                        birthDate = pet.birthDate.toString(),
                        urls = petAttachmentsGroupbyId?.let { attachments ->
                            attachments[pet.id.value]?.map {
                                fileUploadService.getSignedUrl(it!!.bucket, it.key, it.filename).toString()
                            }
                        } ?: listOf()
                    )
                }
            )
        } catch (e: Exception) {
            logger.warn("get pets fail", e)
            throw e
        }
    }

    fun getPetHistories(
        petIds: List<Long>?,
        managerId: Long?,
        contentType: String?,
        content: String?,
        showOnTop: Int?,
        deleted: Int?,
        limit: Int?,
        offset: Int?
    ): PetHistoryDto {
        try {
            val total = petDao.countHistories(
                petIds = petIds,
                managerId = managerId,
                contentType = contentType,
                content = content,
                showOnTop = showOnTop,
                deleted = deleted
            )
            val petHistories = petDao.searchHistories(
                petIds = petIds,
                managerId = managerId,
                contentType = contentType,
                content = content,
                showOnTop = showOnTop,
                deleted = deleted,
                limit = limit?.let { it } ?: Constants.Paging.DEFAULT_LIMIT,
                offset = offset?.let { it } ?: Constants.Paging.DEFAULT_OFFSET
            )
            return PetHistoryDto(
                total = total,
                histories = petHistories.map {
                    PetHistoryInfo(
                        id = it["id"].toString().toLong(),
                        petId = it["petId"].toString().toLong(),
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
            logger.warn("get pet histories fail", e)
            throw e
        }
    }

    fun getPetById(id: Long): PetDetailDto {
        try {
            val pet = petDao.findById(id)

            val commentsCount = petCommentDao.countPetCommentsByPetId(id)
            val comments = petCommentDao.findPetCommentsByPetId(id)
            val commentDto = PetCommentDto(
                total = commentsCount,
                comments = comments.mapNotNull { comment ->
                    PetCommentInfo(
                        commentId = comment.id.value,
                        petId = comment.petId,
                        userId = comment.userId,
                        parentId = comment.parentId,
                        comment = comment.comment,
                        showOnTop = comment.showOnTop == 1,
                        createdAt = comment.createdAt.toEpochMilli(),
                        updatedAt = comment.updatedAt.toEpochMilli()
                    )
                }
            )

            val diseasesCount = petDiseaseDao.countPetDiseasesByPetId(id)
            val diseases = petDiseaseDao.findPetDiseasesByPetId(id)
            val diseaseDto = PetDiseaseDto(
                total = diseasesCount,
                diseases = diseases.mapNotNull { disease ->
                    val diseaseId = disease.id.value
                    val treatmentHistoriesByDiseaseId = petTreatmentHistoryDao.findPetTreatmentHistoriesByDiseaseId(diseaseId)
                    PetDiseaseInfo(
                        diseaseId,
                        petId = disease.petId,
                        name = disease.name,
                        healed = disease.healed == 1,
                        treatmentHistories = treatmentHistoriesByDiseaseId.map {
                            PetTreatmentHistoryInfo(
                                petId = it.petId,
                                petDiseaseId = it.petDiseaseId,
                                contents = it.contents,
                                treatmentDate = it.treatmentDate.atStartOfDay(Times.KST).toInstant().toEpochMilli()

                            )
                        }
                    )
                }
            )

            val treatmentHistoriesCount = petTreatmentHistoryDao.countPetTreatmentHistoriesByPetId(id)
            val treatmentHistories = petTreatmentHistoryDao.findPetTreatmentHistoriesByPetId(id)
            val treatmentHistoryDto = PetTreatmentHistoryDto(
                total = treatmentHistoriesCount,
                treatmentHistories = treatmentHistories.mapNotNull { treatmentHistory ->
                    PetTreatmentHistoryInfo(
                        treatmentHistoryId = treatmentHistory.id.value,
                        petId = treatmentHistory.petId,
                        petDiseaseId = treatmentHistory.petDiseaseId,
                        contents = treatmentHistory.contents,
                        treatmentDate = treatmentHistory.treatmentDate.atStartOfDay(Times.KST).toInstant().toEpochMilli()
                    )
                }
            )

            val attachment = attachmentDao.findPetAttachmentByPetIds(listOf(id))

            return PetDetailDto(
                id = pet.id.value,
                type = pet.type,
                name = pet.name,
                breeds = pet.breeds,
                gender = pet.gender,
                weight = pet.weight,
                adoptionStatus = pet.adoptionStatus,
                birthDate = pet.birthDate.toString(),
                comments = commentDto,
                diseases = diseaseDto,
                treatmentHistories = treatmentHistoryDto,
                urls = if (attachment.isNotEmpty()) attachment.map { fileUploadService.getSignedUrl(it!!.bucket, it.key, it.filename).toString() } else listOf(),
                creatorId = pet.creatorId,
                updaterId = pet.updaterId,
                createdAt = pet.createdAt.toEpochMilli(),
                updatedAt = pet.updatedAt.toEpochMilli()
            )
        } catch (e: Exception) {
            logger.warn("get pet-$id fail", e)
            throw e
        }
    }

    fun createPet(params: Map<String, Any>): Long {
        val attachments = params["attachments"]?.let { it as List<Map<String, Any>> }
        val type = params["type"].toString()
        val name = params["name"].toString()
        val breeds = params["breeds"].toString()
        val gender = params["gender"].toString()
        val weight = params["weight"].toString().toInt()
        val adoptionStatus = params["adoptionStatus"].toString()
        val birthDate = Instant.ofEpochMilli(params["birthDate"].toString().toLong()).atZone(Times.KST).toLocalDate()
        val managerId = params["managerId"].toString().toLong()
        val creatorId = params["creatorId"].toString()

        try {
            val petId = petDao.create(
                type = type,
                name = name,
                breeds = breeds,
                gender = gender,
                weight = weight,
                adoptionStatus = adoptionStatus,
                birthDate = birthDate,
                creatorId = creatorId
            )

            attachments?.map { attachment ->
                val newObject = fileUploadService.preserve("pet-attachment", attachment["bucket"].toString(), attachment["key"].toString())
                attachmentDao.upsertPetAttachment(
                    petId = petId,
                    type = attachment["type"].toString(),
                    bucket = newObject.bucket,
                    key = newObject.key,
                    filename = attachment["filename"].toString(),
                    updaterId = creatorId
                )
            }

            petDao.createHistory(
                petId = petId,
                contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                content = "pet create - petId: $petId",
                managerId = managerId,
                creatorId = creatorId
            )

            return petId
        } catch (e: Exception) {
            logger.warn("create pet fail", e)
            throw e
        }
    }

    fun createPetComment(params: Map<String, Any>): Long {
        val petId = params["petId"].toString().toLong()
        val userId = params["userId"].toString().toLong()
        val parentId = if (params.containsKey("parentId")) params["parentId"].toString().toLong() else null
        val comment = params["comment"].toString()
        val showOnTop = params["showOnTop"] as Boolean

        try {
            return petCommentDao.create(
                petId = petId,
                userId = userId,
                parentId = parentId,
                comment = comment,
                showOnTop = showOnTop
            )
        } catch (e: Exception) {
            logger.warn("create pet comment fail", e)
            throw e
        }
    }

    fun createPetDisease(params: Map<String, Any>): Long {
        val petId = params["petId"].toString().toLong()
        val name = params["name"].toString()
        val healed = params["healed"] as Boolean
        val creatorId = params["creatorId"].toString()

        try {
            return petDiseaseDao.create(
                petId = petId,
                name = name,
                healed = healed,
                creatorId = creatorId
            )
        } catch (e: Exception) {
            logger.warn("create pet disease fail", e)
            throw e
        }
    }

    fun createPetTreatmentHistory(params: Map<String, Any>): Long {
        val petId = params["petId"].toString().toLong()
        val petDiseaseId = if (params.containsKey("petDiseaseId")) params["petDiseaseId"].toString().toLong() else null
        val contents = params["contents"].toString()
        val treatmentDate = Instant.ofEpochMilli(params["treatmentDate"].toString().toLong()).atZone(Times.KST).toLocalDate()
        val creatorId = params["creatorId"].toString()

        try {
            return petTreatmentHistoryDao.create(
                petId = petId,
                petDiseaseId = petDiseaseId,
                contents = contents,
                treatmentDate = treatmentDate,
                creatorId = creatorId
            )
        } catch (e: Exception) {
            logger.warn("create pet treatment history fail", e)
            throw e
        }
    }

    fun updatePet(params: Map<String, Any>): Boolean {
        val petId = params["petId"].toString().toLong()
        val attachments = params["attachments"]?.let { it as List<Map<String, Any>> }
        val clearAttachments = params["clearAttachments"]?.let { it as Boolean }
        val type = params["type"]?.toString()
        val name = params["name"]?.toString()
        val breeds = params["breeds"]?.toString()
        val gender = params["gender"]?.toString()
        val weight = params["weight"]?.toString()?.toInt()
        val adoptionStatus = params["adoptionStatus"]?.toString()
        val birthDate = params["birthDate"]?.let { Instant.ofEpochMilli(it.toString().toLong()).atZone(Times.KST).toLocalDate() }
        val deleted = params["deleted"]?.toString()?.toInt()
        val managerId = params["managerId"].toString().toLong()
        val updaterId = params["updaterId"].toString()
        val historyContents = mutableListOf<String>()

        try {
            if (listOfNotNull(type, name, breeds, gender, weight, adoptionStatus, birthDate, deleted).count() > 0) {
                val originMap = petDao.findPetToMap(petId)
                val updateMap = mutableMapOf(
                    "type" to type,
                    "name" to name,
                    "breeds" to breeds,
                    "gender" to gender,
                    "weight" to weight,
                    "adoptionStatus" to adoptionStatus,
                    "birthDate" to birthDate,
                    "deleted" to deleted
                )
                historyContents.add(History.getContent(originMap, updateMap, "애완동물 정보 변경"))

                petDao.update(
                    petId = petId,
                    type = type,
                    name = name,
                    breeds = breeds,
                    gender = gender,
                    weight = weight,
                    adoptionStatus = adoptionStatus,
                    birthDate = birthDate,
                    deleted = deleted,
                    updaterId = updaterId
                )

                petDao.createHistory(
                    petId = petId,
                    contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                    content = historyContents.joinToString("\n"),
                    managerId = managerId,
                    creatorId = updaterId
                )
            }

            val prevAttachments = attachmentDao.findPetAttachmentByPetIds(listOf(petId))
            attachments?.let {
                val attachmentIds = attachments?.mapNotNull { attachment ->
                    val attachmentId = attachment["attachmentId"]?.toString()?.toLong()
                    val newObject = attachmentId?.let {
                        fileUploadService.preserve("pet-attachment", attachment["bucket"].toString(), attachment["key"].toString())
                    }
                    if (attachment.count() > 0) {
                        attachmentDao.upsertPetAttachment(
                            petId = petId,
                            type = attachment["type"].toString(),
                            bucket = newObject?.bucket,
                            key = newObject?.key,
                            filename = attachment["filename"].toString(),
                            id = attachmentId,
                            updaterId = updaterId
                        )
                    }
                    attachmentId
                }

                // 삭제 요청된 이전 첨부파일 삭제
                val prevAttachmentIdsToDelete = if (prevAttachments.isNotEmpty()) prevAttachments.mapNotNull {
                    if (!attachmentIds.contains(it!!.id.value)) {
                        it.id.value
                    } else null
                } else listOf()
                if (prevAttachmentIdsToDelete.count() > 0) {
                    attachmentDao.deletePetAttachment(prevAttachmentIdsToDelete, updaterId = updaterId)
                }
            }

            if (clearAttachments !== null && clearAttachments) {
                if (prevAttachments.isNotEmpty()) attachmentDao.deletePetAttachment(prevAttachments.map { it!!.id.value }, updaterId = updaterId)
            }

            return true
        } catch (e: Exception) {
            logger.warn("update pet fail", e)
            throw e
        }
    }

    fun updatePetComment(params: Map<String, Any>): Boolean {
        val commentId = params["commentId"].toString().toLong()
        val comment = if (params.containsKey("comment")) params["comment"].toString() else null
        val showOnTop = if (params.containsKey("showOnTop")) params["showOnTop"] as Boolean else null
        val deleted = if (params.containsKey("deleted")) params["deleted"] as Boolean else null

        try {
            petCommentDao.update(
                commentId = commentId,
                comment = comment,
                showOnTop = showOnTop,
                deleted = deleted
            )
            return true
        } catch (e: Exception) {
            logger.warn("update pet comment fail", e)
            throw e
        }
    }

    fun updatePetDisease(params: Map<String, Any>): Boolean {
        val diseaseId = params["diseaseId"].toString().toLong()
        val name = params["name"]?.toString()
        val healed = params["healed"]?.let { it as Boolean }
        val deleted = params["deleted"]?.let { it as Boolean }
        val updaterId = params["updaterId"].toString()
        val managerId = params["managerId"].toString().toLong()
        val historyContents = mutableListOf<String>()

        try {
            if (listOfNotNull(name, healed, deleted).count() > 0) {
                val originMap = petDiseaseDao.findPetDiseaseToMap(diseaseId)
                val updateMap = mutableMapOf(
                    "name" to name,
                    "healed" to healed,
                    "deleted" to deleted
                )
                historyContents.add(History.getContent(originMap, updateMap, "애완동물 질병 정보 변경"))

                petDiseaseDao.update(
                    diseaseId = diseaseId,
                    name = name,
                    healed = healed,
                    deleted = deleted,
                    updaterId = updaterId
                )

                petDao.createHistory(
                    petId = originMap?.get("petId").toString().toLong(),
                    contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                    content = historyContents.joinToString("\n"),
                    managerId = managerId,
                    creatorId = updaterId
                )
            }
            return true
        } catch (e: Exception) {
            logger.warn("update pet disease fail", e)
            throw e
        }
    }

    fun updatePetTreatmentHistory(params: Map<String, Any>): Boolean {
        val treatmentHistoryId = params["treatmentHistoryId"].toString().toLong()
        val contents = params["contents"]?.toString()
        val treatmentDate = params["treatmentDate"]?.let { Instant.ofEpochMilli(it.toString().toLong()).atZone(Times.KST).toLocalDate() }
        val deleted = params["deleted"]?.let { it as Boolean }
        val updaterId = params["updaterId"].toString()
        val managerId = params["managerId"].toString().toLong()
        val historyContents = mutableListOf<String>()

        try {
            if (listOfNotNull(contents, treatmentDate, deleted).count() > 0) {
                val originMap = petTreatmentHistoryDao.findPetTreatmentHistoryToMap(treatmentHistoryId)
                val updateMap = mutableMapOf(
                    "contents" to contents,
                    "treatmentDate" to treatmentDate,
                    "deleted" to deleted
                )
                historyContents.add(History.getContent(originMap, updateMap, "애완동물 치료내역 변경"))

                petTreatmentHistoryDao.update(
                    treatmentHistoryId = treatmentHistoryId,
                    contents = contents,
                    treatmentDate = treatmentDate,
                    deleted = deleted,
                    updaterId = updaterId
                )

                petDao.createHistory(
                    petId = originMap?.get("petId").toString().toLong(),
                    contentType = Codes.HistoryContentType.CHANGE_LOG.value,
                    content = historyContents.joinToString("\n"),
                    managerId = managerId,
                    creatorId = updaterId
                )
            }
            return true
        } catch (e: Exception) {
            logger.warn("update pet treatment history fail", e)
            throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun batchDeletePetHistory(deleteParams: Map<String, Any>): Boolean {
        val targetIds = deleteParams["targetIds"] as List<Long>
        val updaterId = deleteParams["updaterId"].toString()

        try {
            petDao.batchDeletePetHistory(targetIds, updaterId)
            return true
        } catch (e: Exception) {
            logger.warn("batch delete pet history fail", e)
            throw e
        }
    }
}