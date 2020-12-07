package site.jonus.savog.api.dto

data class PetListDto(
    val id: Long? = 0,
    val type: String? = "",
    val name: String? = "",
    val breeds: String? = "",
    val gender: String? = "",
    val adoptionStatus: String? = "",
    val birthDate: String? = ""
)