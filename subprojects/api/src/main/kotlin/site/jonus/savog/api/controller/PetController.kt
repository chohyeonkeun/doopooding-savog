package site.jonus.savog.api.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import site.jonus.savog.api.ResultJson
import site.jonus.savog.api.service.PetService

@RestController
@RequestMapping("/v1", name = "두푸딩 세이보그 유기동물")
class PetController(private val petService: PetService) : BaseController() {
    @GetMapping("/pets", name = "세이보그 유기동물 목록 조회")
    fun getPets(
        @RequestParam("id") ids: List<Long>?,
        @RequestParam("type") type: String?,
        @RequestParam("name") name: String?,
        @RequestParam("breeds") breeds: String?,
        @RequestParam("gender") gender: String?,
        @RequestParam("adoptionStatus") adoptionStatus: String?,
        @RequestParam("birthStDate") birthStDate: Long?,
        @RequestParam("birthEdDate") birthEdDate: Long?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("offset") offset: Int?
    ): ResultJson {
        return try {
            ResultJson.withData(
                petService.getPets(
                    ids = ids,
                    type = type,
                    name = name,
                    breeds = breeds,
                    gender = gender,
                    adoptionStatus = adoptionStatus,
                    birthStDate = birthStDate,
                    birthEdDate = birthEdDate,
                    limit = limit,
                    offset = offset
                )
            )
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "get pets fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @GetMapping("/pet/histories", name = "세이보그 유기동물 히스토리 목록 조회")
    fun getPetHistories(
        @RequestParam("petIds") petIds: List<Long>?,
        @RequestParam("managerId") managerId: Long?,
        @RequestParam("contentType") contentType: String?,
        @RequestParam("content") content: String?,
        @RequestParam("showOnTop") showOnTop: Int?,
        @RequestParam("deleted") deleted: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("offset") offset: Int?
    ): ResultJson {
        return try {
            ResultJson.withData(
                petService.getPetHistories(
                    petIds = petIds,
                    managerId = managerId,
                    contentType = contentType,
                    content = content,
                    showOnTop = showOnTop,
                    deleted = deleted,
                    limit = limit,
                    offset = offset
                )
            )
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "get pet histories fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @GetMapping("/pets/{id}", name = "세이보그 유기동물 상세정보 조회")
    fun getPetById(@PathVariable id: Long): ResultJson {
        return try {
            ResultJson.withData(petService.getPetById(id))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "get pet-$id fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @PostMapping("/pets", name = "세이보그 유기동물 등록")
    fun createPet(@RequestBody createParams: Map<String, Any>): ResultJson {
        return try {
            ResultJson.withData(petService.createPet(createParams))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "create pet fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @PostMapping("/pet/comments", name = "세이보그 유기동물 댓글 등록")
    fun createPetComment(@RequestBody createParams: Map<String, Any>): ResultJson {
        return try {
            ResultJson.withData(petService.createPetComment(createParams))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "create pet comment fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @PutMapping("/pets", name = "세이보그 유기동 수정")
    fun updatePet(@RequestBody updateParams: Map<String, Any>): ResultJson {
        return try {
            ResultJson.withData(petService.updatePet(updateParams))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "update pet fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @PutMapping("/pet/comments", name = "세이보그 유기동물 댓글 등록")
    fun updatePetComment(@RequestBody updateParams: Map<String, Any>): ResultJson {
        return try {
            ResultJson.withData(petService.updatePetComment(updateParams))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "update pet comment fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @DeleteMapping("/pet/histories", name = "세이보그 유기동물 히스토리 일괄 삭제")
    fun batchDeletePetHistory(@RequestBody deleteParams: Map<String, Any>): ResultJson {
        return try {
            ResultJson.withData(petService.batchDeletePetHistory(deleteParams))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "batch delete pet histories fail",
                        message = e.message
                    )
                )
            )
        }
    }
}