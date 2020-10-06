package site.jonus.savog.api.service

import site.jonus.savog.api.configuration.security.JwtTokenProvider
import site.jonus.savog.api.dto.LoginUserDto
import site.jonus.savog.core.Codes
import site.jonus.savog.core.dao.UserDao
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

    fun join(joinParams: Map<String, Any>): Long {
        try {
            val name = joinParams["name"].toString()
            val email = joinParams["email"].toString()
            val password = passwordEncoder.encode(joinParams["password"].toString())
            val nickname = joinParams["nickname"].toString()
            val roles = if (joinParams["roles"] != null) joinParams["roles"] as List<String> else listOf(Codes.UserRoleType.GENERAL.value)
            val loginType = joinParams["loginType"].toString()

            if (userDao.findByEmail(email) != null) {
                // TODO : 이메일 중복 예외 처리
                throw IllegalArgumentException("동일한 이메일이 이미 등록되어 있습니다.")
            } else {
                val userId = userDao.create(
                    name = name,
                    email = email,
                    password = password,
                    nickname = nickname,
                    loginType = loginType
                )
                userDao.batchCreateRolesByUserId(userId, roles = roles)
                return userId
            }
        } catch (e: SQLException) {
            logger.warn("user join fail", e)
            throw SQLException(e)
        } catch (e: IllegalArgumentException) {
            logger.warn("user join fail, e")
            throw IllegalArgumentException(e)
        }
    }

    fun login(loginParams: Map<String, Any>): LoginUserDto {
        try {
            var name: String
            var email: String
            var roles: List<String>
            var authToken: String?
            var nickname: String
            val loginType = loginParams["loginType"].toString()

            return if (loginType == _root_ide_package_.site.jonus.savog.core.Codes.LoginType.SNS_NAVER.value) {
                // 네이버 아이디로 로그인한 경우
                name = loginParams["name"].toString()
                email = loginParams["email"].toString()
                roles = listOf(_root_ide_package_.site.jonus.savog.core.Codes.UserRoleType.GENERAL.value)
                authToken = jwtTokenProvider.createToken(email, roles)
                nickname = loginParams["nickname"].toString()
                val user = userDao.findByEmail(email)
                val userId = user?.id?.value
                    ?: userDao.create(
                        name = name,
                        email = email,
                        nickname = nickname,
                        loginType = loginType
                    )

                userDao.batchCreateRolesByUserId(userId, roles = roles)

                LoginUserDto(
                    userId = userId,
                    userName = name,
                    userEmail = email,
                    userNickname = nickname,
                    loginType = loginType,
                    authToken = authToken,
                    userRole = roles
                )
            } else {
                // 이메일로 로그인한 경우
                val user = userDao.findByEmail(loginParams["email"].toString())
                    ?: throw IllegalArgumentException("가입되지 않은 E-MAIL입니다.")

                email = user.email
                name = user.name
                nickname = user.nickname
                roles = userDao.findRolesByEmail(loginParams["email"].toString())?.map { it.role }
                    ?: throw IllegalArgumentException("해당 E-MAIL에 대한 어떠한 권한도 없습니다.")
                authToken = jwtTokenProvider.createToken(email, roles)

                if (!passwordEncoder.matches(loginParams["password"].toString(), user.password)) {
                    throw IllegalArgumentException("잘못된 비밀번호입니다.")
                }

                LoginUserDto(
                    userId = user.id.value,
                    userName = name,
                    userEmail = email,
                    userNickname = nickname,
                    loginType = loginType,
                    authToken = authToken,
                    userRole = roles
                )
            }
        } catch (e: RuntimeException) {
            logger.warn("user login fail", e)
            throw IllegalArgumentException("잘못된 로그인 정보입니다.", e)
        }
    }
}