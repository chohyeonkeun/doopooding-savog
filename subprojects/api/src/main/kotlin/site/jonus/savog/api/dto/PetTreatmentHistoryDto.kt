package site.jonus.savog.api.dto

data class PetTreatmentHistoryDto(
    val total: Int,
    val treatmentHistories: List<PetTreatmentHistoryInfo>
)

data class PetTreatmentHistoryInfo(
    val treatmentHistoryId: Long? = null,
    val petId: Long? = null,
    val petDiseaseId: Long? = null,
    val contents: String? = null,
    val treatmentDate: Long? = null
)