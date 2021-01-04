package site.jonus.savog.api.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import site.jonus.savog.api.ResultJson
import site.jonus.savog.api.service.SponsorshipFeeService

@RestController
@RequestMapping("/v1", name = "세이보그 후원금")
class SponsorshipFeeController(private val sponsorshipFeeService: SponsorshipFeeService) : BaseController() {
    @GetMapping("/sponsorshipFees", name = "세이보그 후원금 전체 조회")
    fun getSponsorshipFees(
        @RequestParam("id") ids: List<Long>?,
        @RequestParam("petId") petIds: List<Long>?,
        @RequestParam("status") status: String?,
        @RequestParam("creatorId") creatorId: String?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("offset") offset: Int?
    ): ResultJson {
        return try {
            ResultJson.withData(
                sponsorshipFeeService.getSponsorshipFees(
                    ids = ids,
                    petIds = petIds,
                    status = status,
                    creatorId = creatorId,
                    limit = limit,
                    offset = offset
                )
            )
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "get sponsorship fees fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @GetMapping("/sponsorshipFee/histories", name = "세이보그 후원금 히스토리 목록 조회")
    fun getSponsorshipFeeHistories(
        @RequestParam("sponsorshipFeeId") sponsorshipFeeIds: List<Long>?,
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
                sponsorshipFeeService.getSponsorshipFeeHistories(
                    sponsorshipFeeIds = sponsorshipFeeIds,
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
                        code = "get sponsorship fee histories fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @GetMapping("/sponsorshipFee/transaction/histories", name = "세이보그 후원금 내역 조회")
    fun getSponsorshipFeeTransactionHistories(
        @RequestParam("sponsorshipFeeId") sponsorshipFeeIds: List<Long>?,
        @RequestParam("transactionType") transactionType: String?,
        @RequestParam("target") target: String?,
        @RequestParam("transactionStDate") transactionStDate: Long?,
        @RequestParam("transactionEdDate") transactionEdDate: Long?,
        @RequestParam("creatorId") creatorId: String?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("offset") offset: Int?
    ): ResultJson {
        return try {
            ResultJson.withData(
                sponsorshipFeeService.getTransactionHistories(
                    sponsorshipFeeIds = sponsorshipFeeIds,
                    transactionType = transactionType,
                    target = target,
                    transactionStDate = transactionStDate,
                    transactionEdDate = transactionEdDate,
                    creatorId = creatorId,
                    limit = limit,
                    offset = offset
                )
            )
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "get sponsorship fee transaction histories fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @PostMapping("/sponsorshipFees", name = "세이보그 후원금 등록")
    fun createSponsorshipFee(
        @RequestBody createParams: Map<String, Any>,
        @RequestHeader("X-Requester-Id") requesterId: String,
        @RequestHeader("X-Requester-Username") requesterUsername: String
    ): ResultJson {
        val mutableParams = createParams.toMutableMap()
        mutableParams["requesterId"] = requesterId
        mutableParams["requesterUsername"] = requesterUsername
        return try {
            ResultJson.withData(sponsorshipFeeService.createSponsorshipFee(mutableParams.toMap()))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "create sponsorship fee fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @PostMapping("/sponsorshipFee/transaction/histories", name = "세이보그 후원금 내역 등록")
    fun createTransactionHistory(
        @RequestBody createParams: Map<String, Any>,
        @RequestHeader("X-Requester-Id") requesterId: String,
        @RequestHeader("X-Requester-Username") requesterUsername: String
    ): ResultJson {
        val mutableParams = createParams.toMutableMap()
        mutableParams["requesterId"] = requesterId
        mutableParams["requesterUsername"] = requesterUsername
        return try {
            ResultJson.withData(sponsorshipFeeService.createTransactionHistory(mutableParams.toMap()))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "create sponsorship fee transaction history fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @PutMapping("/sponsorshipFees", name = "세이보그 후원금 수정")
    fun updateSponsorshipFee(
        @RequestBody updateParams: Map<String, Any>,
        @RequestHeader("X-Requester-Id") requesterId: String,
        @RequestHeader("X-Requester-Username") requesterUsername: String
    ): ResultJson {
        val mutableParams = updateParams.toMutableMap()
        mutableParams["requesterId"] = requesterId
        mutableParams["requesterUsername"] = requesterUsername
        return try {
            ResultJson.withData(sponsorshipFeeService.updateSponsorshipFee(mutableParams.toMap()))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "update sponsorship fee fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @PutMapping("/sponsorshipFee/transaction/histories", name = "세이보그 후원금 내역 수정")
    fun updateTransactionHistory(
        @RequestBody updateParams: Map<String, Any>,
        @RequestHeader("X-Requester-Id") requesterId: String,
        @RequestHeader("X-Requester-Username") requesterUsername: String
    ): ResultJson {
        val mutableParams = updateParams.toMutableMap()
        mutableParams["requesterId"] = requesterId
        mutableParams["requesterUsername"] = requesterUsername
        return try {
            ResultJson.withData(sponsorshipFeeService.updateTransactionHistory(mutableParams.toMap()))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "update sponsorship fee transaction history fail",
                        message = e.message
                    )
                )
            )
        }
    }

    @DeleteMapping("/sponsorshipFees", name = "세이보그 후원금 삭제")
    fun batchDeleteSponsorshipFee(
        @RequestBody deleteParams: Map<String, Any>,
        @RequestHeader("X-Requester-Id") requesterId: String,
        @RequestHeader("X-Requester-Username") requesterUsername: String
    ): ResultJson {
        val mutableParams = deleteParams.toMutableMap()
        mutableParams["requesterId"] = requesterId
        mutableParams["requesterUsername"] = requesterUsername
        return try {
            ResultJson.withData(sponsorshipFeeService.deleteSponsorshipFee(mutableParams.toMap()))
        } catch (e: Exception) {
            ResultJson.withError(
                errors = *arrayOf(
                    ResultJson.Error(
                        code = "batch delete sponsorship fee fail",
                        message = e.message
                    )
                )
            )
        }
    }
}
