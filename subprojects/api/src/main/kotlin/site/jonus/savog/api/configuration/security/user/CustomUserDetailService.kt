package site.jonus.savog.api.configuration.security.user

import site.jonus.savog.api.configuration.security.ConvertToUserDetails
import site.jonus.savog.core.dao.UserDao
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CustomUserDetailService(private val userDao: UserDao) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userDao.findByEmail(username)
        return if (user !== null) {
            ConvertToUserDetails(user, userDao)
        } else throw UsernameNotFoundException("사용자를 찾을 수 없습니다.")
    }
}
