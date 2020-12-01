package site.jonus.savog.api.service

import org.springframework.stereotype.Service

@Service
class PetService : BaseService() {
    fun getPets(
        ids: List<String>?,
        type: String?,
        name: String?,
        breeds: String?,
        gender: String?,
        weight: Int?,
        adoptionState: String?,
        birthDate: String?
    ) {

    }

    fun getPetById(id: Long) {
    }

    fun createPet(params: Map<String, Any>) {
    }

    fun updatePet(params: Map<String, Any>) {
    }

    fun batchDeletePet(params: Map<String, Any>) {
    }
}