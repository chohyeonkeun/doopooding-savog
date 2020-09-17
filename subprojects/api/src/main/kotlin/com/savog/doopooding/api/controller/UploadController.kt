package com.savog.doopooding.api.controller

import com.savog.doopooding.api.ResultJson
import com.savog.doopooding.core.service.FileUploadService
import com.savog.doopooding.core.util.UploadConfig
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.WebUtils

@RestController
@RequestMapping("/v1", name = "파일 업로드")
class UploadController(private val fileUploadService: FileUploadService) : BaseController() {

    @PostMapping("/files/upload", name = "업로드")
    fun requestUpload(
        @RequestParam(required = false, defaultValue = "false") isDev: Boolean,
        @RequestParam(required = false, defaultValue = "default") type: String,
        @RequestParam(required = false) targetId: Long?,
        @RequestParam file: MultipartFile
    ): ResultJson {
        val allowTypes = UploadConfig.getType(type).allowTypes
        val obj = fileUploadService.upload(file, type, allowMediaTypes = allowTypes, isDev = isDev)
        val signedUrl = fileUploadService.getSignedUrl(obj.bucket, obj.key)
        val map = mutableMapOf(
            "result" to true,
            "bucket" to obj.bucket,
            "key" to obj.key,
            "url" to signedUrl
        )
        obj.width?.let { map["width"] = it }
        obj.height?.let { map["height"] = it }
        targetId?.let { map["targetId"] = it }
        return ResultJson.withData(map)
    }

    @ExceptionHandler(value = [IllegalArgumentException::class])
    fun handleError(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<Any>? {
        val headers = HttpHeaders()
        val status = HttpStatus.BAD_REQUEST
        val error = ResultJson.Error("UPLOAD_FAIL", "허용하지 않는 확장자 입니다.")

        if (HttpStatus.INTERNAL_SERVER_ERROR == status) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST)
        }
        return ResponseEntity(ResultJson.withError(error), headers, status)
    }
}