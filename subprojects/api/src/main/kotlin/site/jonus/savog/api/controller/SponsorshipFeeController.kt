package site.jonus.savog.api.controller

import site.jonus.savog.api.ResultJson
import site.jonus.savog.api.service.SponsorshipFeeService
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
@RequestMapping("/v1", name = "세이보그 후원금")
class SponsorshipFeeController(private val sponsorshipFeeService: SponsorshipFeeService) : BaseController() {
    @GetMapping("/sponsorshipFees", name = "세이보그 유기 애완동물 후원금 전체 조회")
    fun getSponsorshipFees(
        @RequestParam petIds: List<Long>?,
        @RequestParam status: String?
    ): ResultJson {
        return ResultJson.withData(sponsorshipFeeService.getSponsorshipFees())
    }

    @GetMapping("/sponsorshipFees/{petId}", name = "세이보그 유기 애완동물 후원금 조회")
    fun getSponsorshipFeesByPetId(@PathVariable petId: Long): ResultJson {
        return ResultJson.withData(sponsorshipFeeService.getSponsorshipFeesByPetId(petId))
    }

    @PostMapping("/sponsorshipFees", name = "세이보그 유기 애완동물 후원금 등록")
    fun createSponsorshipFee(@RequestBody createParams: Map<String, Any>): Any {
        return ResultJson.withData(sponsorshipFeeService.createSponsorshipFee(createParams))
    }

    @PutMapping("/sponsorshipFees", name = "세이보그 유기 애완동물 후원금 수정")
    fun updateSponsorshipFee(@RequestBody updateParams: Map<String, Any>): Any {
        return ResultJson.withData(sponsorshipFeeService.updateSponsorshipFee(updateParams))
    }

    @DeleteMapping("/sponsorshipFees", name = "세이보그 유기 애완동물 후원금 삭제")
    fun batchDeleteSponsorshipFee(@RequestBody deleteParams: Map<String, Any>): Any {
        return ResultJson.withData(sponsorshipFeeService.deleteSponsorshipFee(deleteParams))
    }
}