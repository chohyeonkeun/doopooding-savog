package site.jonus.savog.api.controller

import site.jonus.savog.api.ResultJson
import site.jonus.savog.api.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", name = "두푸딩 세이보그 회원")
class UserController(private val userService: UserService) : BaseController() {
    // 회원가입
    @PostMapping("/users/join")
    fun join(@RequestBody user: Map<String, Any>): ResultJson {
        return ResultJson.withData(userService.join(user))
    }

    // 로그인
    @PostMapping("/users/login")
    fun login(@RequestBody user: Map<String, Any>): ResultJson {
        return ResultJson.withData(userService.login(user))
    }
}