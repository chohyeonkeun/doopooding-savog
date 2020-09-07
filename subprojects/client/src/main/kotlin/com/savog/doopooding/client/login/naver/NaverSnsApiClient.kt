package com.savog.doopooding.client.login.naver

import com.savog.doopooding.client.login.SnsClientProperties
import com.savog.doopooding.client.login.dto.GetLoginUriDto
import com.savog.doopooding.client.login.dto.GetUserInfoDto
import com.savog.doopooding.client.login.dto.RequestTokenDto
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

class NaverSnsApiClient(
    private val snsClientProperties: SnsClientProperties,
    webClientBuilder: WebClient.Builder
) {
    private val tokenCheckUrl = "https://nid.naver.com/oauth2.0/token"
    private val userMeUrl = "https://openapi.naver.com/v1/nid/me" // 네이버 회원 프로필 조회
    private val webClient = webClientBuilder.build()

    fun getLoginUri(getLoginUriDto: GetLoginUriDto): String {
        val redirectUri = getLoginUriDto.redirectUri
        val state = getLoginUriDto.state
        return "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${snsClientProperties.naver.clientId}&state=$state&redirect_uri=$redirectUri"
    }

    fun requestTokenAndReturnUserInfo(requestTokenDto: RequestTokenDto): GetUserInfoDto {
        val code = requestTokenDto.code
        val state = requestTokenDto.state
        val monoToken: Mono<Map<String, Any>> = webClient
            .mutate()
            .baseUrl(tokenCheckUrl)
            .build()
            .get()
            .uri {
                it.queryParam("client_id", snsClientProperties.naver.clientId)
                it.queryParam("client_secret", snsClientProperties.naver.clientSecret)
                it.queryParam("code", code)
                it.queryParam("grant_type", "authorization_code")
                it.queryParam("state", state)
                    .build()
            }
            .retrieve().bodyToMono()
        val token = monoToken.block()!!
        val monoUserInfo: Mono<Map<String, Any?>> = webClient.mutate().baseUrl(userMeUrl).build().post().header("Authorization", "Bearer ${token["access_token"]}").retrieve().bodyToMono()
        val userInfo = monoUserInfo.block()!!["response"] as Map<String, Any>

        return GetUserInfoDto(
            id = userInfo["id"].toString(),
            name = userInfo["name"].toString(),
            email = userInfo["email"].toString(),
            nickname = userInfo["nickname"].toString()
        )
    }
}