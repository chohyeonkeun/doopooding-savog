package site.jonus.savog.api.dto

data class PetDiseaseDto(
    val total: Int,
    val diseases: List<PetDiseaseInfo>
)

data class PetDiseaseInfo(
    val diseaseId: Long? = null,
    val petId: Long? = null,
    val name: String? = null,
    val healed: Boolean? = null,
    val treatmentHistories: List<PetTreatmentHistoryInfo>?
)