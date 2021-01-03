package site.jonus.savog.api.dto

data class PetDetailDto(
    val id: Long,
    val type: String,
    val name: String,
    val breeds: String,
    val gender: String,
    val weight: Int,
    val adoptionStatus: String,
    val birthDate: String,
    val comments: PetCommentDto,
    val diseases: PetDiseaseDto,
    val treatmentHistories: PetTreatmentHistoryDto,
    val urls: List<String?>,
    val creatorId: String,
    val updaterId: String,
    val createdAt: Long,
    val updatedAt: Long
)
