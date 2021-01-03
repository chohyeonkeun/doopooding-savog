package site.jonus.savog.api.dto

data class PetDto(
    val total: Int,
    val pets: List<PetInfo>
)

data class PetInfo(
    val id: Long,
    val type: String,
    val name: String,
    val breeds: String,
    val gender: String,
    val adoptionStatus: String,
    val birthDate: String,
    val urls: List<String?>
)
