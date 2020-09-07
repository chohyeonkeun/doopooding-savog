package com.savog.doopooding.api.dto

data class LoginSnsUserDto(
    var userEmail: String,
    var userName: String,
    var loginType: String,
    var userRole: List<String>
) : LoginUserDto()