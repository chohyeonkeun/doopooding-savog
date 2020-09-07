package com.savog.doopooding.api.dto

data class LoginEmailUserDto(
    var userEmail: String,
    var userName: String,
    var loginType: String,
    var authToken: String,
    var userRole: List<String>
) : LoginUserDto()