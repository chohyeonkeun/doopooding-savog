package site.jonus.savog.api.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import site.jonus.savog.api.dto.PetDetailDto
import site.jonus.savog.api.dto.PetHistoryListDto
import site.jonus.savog.api.dto.PetListDto
import site.jonus.savog.core.Constants
import site.jonus.savog.core.dao.PetDao
import java.time.Instant
import java.time.ZoneId

@Service
class PetService(private val petDao: PetDao) : BaseService() {
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
    ): List<PetListDto> {
        try {
            val total = petDao.count(
                ids = ids,
                type = type,
                name = name,
                breeds = breeds,
                gender = gender,
                adoptionStatus = adoptionStatus,
                birthStDate = birthStDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("Asia/Seoul")).toLocalDate() },
                birthEdDate = birthEdDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("Asia/Seoul")).toLocalDate() }
            )
            val pets = petDao.search(
                ids = ids,
                type = type,
                name = name,
                breeds = breeds,
                gender = gender,
                adoptionStatus = adoptionStatus,
                birthStDate = birthStDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("Asia/Seoul")).toLocalDate() },
                birthEdDate = birthEdDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("Asia/Seoul")).toLocalDate() },
                limit = limit?.let { it } ?: Constants.Paging.DEFAULT_LIMIT,
                offset = offset?.let { it } ?: Constants.Paging.DEFAULT_OFFSET
            )

            return pets.map {
                PetListDto(
                    total = total,
                    id = it?.id?.value,
                    type = it?.type,
                    name = it?.name,
                    breeds = it?.breeds,
                    gender = it?.gender,
                    adoptionStatus = it?.adoptionStatus,
                    birthDate = it?.birthDate?.toString()
                )
            }
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
    ): List<PetHistoryListDto> {
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

            return petHistories.map {
                PetHistoryListDto(
                    total = total,
                    id = it["id"].toString().toLong(),
                    petId = it["petId"].toString().toLong(),
                    categoryId = it["categoryId"].toString().toLong(),
                    managerId = it["managerId"].toString().toLong(),
                    contentType = it["contentType"].toString(),
                    content = it["content"].toString(),
                    creatorId = it["creatorId"].toString(),
                    updaterId = it["updaterId"].toString(),
                    showOnTop = it["showOnTop"].toString().toInt() == 1,
                    deleted = it["deleted"].toString().toInt() == 1,
                    createdAt = (it["createdAt"] as Instant).toEpochMilli(),
                    updatedAt = (it["updatedAt"] as Instant).toEpochMilli()
                )
            }
        } catch (e: Exception) {
            logger.warn("get pet histories fail", e)
            throw e
        }
    }

    fun getPetById(id: Long): PetDetailDto {
        try {
            val pet = petDao.findById(id)

            return PetDetailDto(
                id = pet.id.value,
                type = pet.type,
                name = pet.name,
                breeds = pet.breeds,
                gender = pet.gender,
                weight = pet.weight,
                adoptionStatus = pet.adoptionStatus,
                birthDate = pet.birthDate.toString(),
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
        val type = params["type"].toString()
        val name = params["name"].toString()
        val breeds = params["breeds"].toString()
        val gender = params["gender"].toString()
        val weight = params["weight"].toString().toInt()
        val adoptionStatus = params["adoptionStatus"].toString()
        val birthDate = Instant.ofEpochMilli(params["birthDate"].toString().toLong()).atZone(ZoneId.of("Asia/Seoul")).toLocalDate()
        val creatorId = params["creatorId"].toString()

        try {
            return petDao.create(
                type = type,
                name = name,
                breeds = breeds,
                gender = gender,
                weight = weight,
                adoptionStatus = adoptionStatus,
                birthDate = birthDate,
                creatorId = creatorId
            )
        } catch (e: Exception) {
            logger.warn("create pet fail", e)
            throw e
        }
    }

    fun updatePet(params: Map<String, Any>): Boolean {
        val petId = params["petId"].toString().toLong()
        val type = params["type"]?.toString()
        val name = params["name"]?.toString()
        val breeds = params["breeds"]?.toString()
        val gender = params["gender"]?.toString()
        val weight = params["weight"]?.toString()?.toInt()
        val adoptionStatus = params["adoptionStatus"]?.toString()
        val birthDate = params["birthDate"]?.let { Instant.ofEpochMilli(it.toString().toLong()).atZone(ZoneId.of("Asia/Seoul")).toLocalDate() }
        val deleted = params["deleted"]?.toString()?.toInt()
        val updaterId = params["updaterId"].toString()

        try {
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

            return true
        } catch (e: Exception) {
            logger.warn("update pet fail", e)
            throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun batchDeletePet(deleteParams: Map<String, Any>): Boolean {
        val targetIds = deleteParams["targetIds"] as List<Long>
        val updaterId = deleteParams["updaterId"].toString()

        try {
            petDao.batchDeletePetHistory(targetIds, updaterId)

            return true
        } catch (e: Exception) {
            logger.warn("batch delete pet fail", e)
            throw e
        }
    }
}