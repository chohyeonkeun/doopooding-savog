package com.savog.doopooding.api.service

import com.savog.doopooding.api.configuration.security.JwtTokenProvider
import com.savog.doopooding.core.Codes
import com.savog.doopooding.core.dao.UserDao
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class UserService(
    private val userDao: UserDao,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) : BaseService() {
    private val logger = LoggerFactory.getLogger(UserService::class.simpleName)

    fun join(user: Map<String, Any>): Long {
        try {
            val roles = if (user["roles"] != null) user["roles"] as List<String> else listOf(Codes.UserRoleType.GENERAL.value)

            val userId = userDao.create(
                email = user["email"].toString(),
                password = passwordEncoder.encode(user["password"].toString()),
                nickname = user["nickname"].toString(),
                type = user["type"].toString()
            )

            userDao.batchCreateRolesByUserId(userId, roles = roles)
            return userId
        } catch (e: Exception) {
            logger.warn("user join fail", e)
            throw SQLException(e)
        }
    }

    fun login(user: Map<String, Any>): String {
        try {
            val member = userDao.findByEmail(user["email"].toString())
                ?: throw IllegalArgumentException("가입되지 않은 E-MAIL입니다.")

            val roles = userDao.findRolesByEmail(user["email"].toString())
                ?: throw IllegalArgumentException("해당 E-MAIL에 대한 어떠한 권한도 없습니다.")

            if (!passwordEncoder.matches(user["password"].toString(), member.password)) {
                throw IllegalArgumentException("잘못된 비밀번호입니다.")
            }

            return jwtTokenProvider.createToken(member.email, roles.map { it.role })
        } catch (e: RuntimeException) {
            logger.warn("user login fail, e")
            throw IllegalArgumentException("잘못된 로그인 정보입니다.", e)
        }
    }
}