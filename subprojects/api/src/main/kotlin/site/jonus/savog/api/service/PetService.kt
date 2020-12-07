package site.jonus.savog.api.service

import org.springframework.stereotype.Service
import site.jonus.savog.api.dto.PetDetailDto
import site.jonus.savog.api.dto.PetListDto
import site.jonus.savog.core.Constants
import site.jonus.savog.core.dao.PetDao
import java.time.Instant
import java.time.ZoneId

@Service
class PetService(private val petDao: PetDao) : BaseService() {
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
                id = it?.id?.value,
                type = it?.type,
                name = it?.name,
                breeds = it?.breeds,
                gender = it?.gender,
                adoptionStatus = it?.adoptionStatus,
                birthDate = it?.birthDate?.toString()
            )
        }
    }

    fun getPetById(id: Long): PetDetailDto {
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
    }

    fun createPet(params: Map<String, Any>) {
    }

    fun updatePet(params: Map<String, Any>) {
    }

    fun batchDeletePet(params: Map<String, Any>) {
    }
}