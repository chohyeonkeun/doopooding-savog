package com.savog.doopooding.client.login.dto

data class RequestTokenDto(
    var provider: String,
    var code: String,
    var state: String?
)

data class GetLoginUriDto(
    var provider: String,
    var redirectUri: String,
    var state: String?
)

data class GetUserInfoDto(
    var id: String,
    var name: String,
    var email: String,
    var nickname: String
)