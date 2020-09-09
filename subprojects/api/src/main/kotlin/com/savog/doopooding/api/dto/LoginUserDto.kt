package com.savog.doopooding.api.dto

data class LoginUserDto(
    val userId: Long,
    var userName: String,
    var userEmail: String,
    var userNickname: String,
    var loginType: String,
    var authToken: String,
    var userRole: List<String>
)