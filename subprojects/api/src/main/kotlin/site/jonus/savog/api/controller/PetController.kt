package site.jonus.savog.api.controller

import site.jonus.savog.api.ResultJson
import site.jonus.savog.api.service.PetService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", name = "두푸딩 세이보그 유기동물")
class PetController(private val petService: PetService) : BaseController() {
    @GetMapping("/pets", name = "세이보그 유기동물 조회")
    fun getPets(
        @RequestParam("id") ids: List<String>?,
        @RequestParam("type") type: String?,
        @RequestParam("name") name: String?,
        @RequestParam("breeds") breeds: String?,
        @RequestParam("gender") gender: String?,
        @RequestParam("weight") weight: Int?,
        @RequestParam("adoptionState") adoptionState: String?,
        @RequestParam("birthDate") birthDate: String?
    ): ResultJson {
        return ResultJson.withData(petService.getPets(
            ids = ids,
            type = type,
            name = name,
            breeds = breeds,
            gender = gender,
            weight = weight,
            adoptionState = adoptionState,
            birthDate = birthDate
        ))
    }

    @GetMapping("/pets/{id}", name = "세이보그 유기동물 상세정보 조회")
    fun getPetById(@PathVariable id: Long): ResultJson {
        return ResultJson.withData(petService.getPetById(id))
    }

    @PostMapping("/pets", name = "세이보그 유기동물 등록")
    fun createPet(@RequestBody createParams: Map<String, Any>): ResultJson {
        return ResultJson.withData(petService.createPet(createParams))
    }

    @PutMapping("/pets", name = "세이보그 유기동물 수정")
    fun updatePat(@RequestBody updateParams: Map<String, Any>): ResultJson {
        return ResultJson.withData(petService.updatePet(updateParams))
    }

    @DeleteMapping("/pets", name = "세이보그 유기동물 일괄 삭제")
    fun batchDeletePat(@RequestBody deleteParams: Map<String, Any>): ResultJson {
        return ResultJson.withData(petService.batchDeletePet(deleteParams))
    }
}