package site.jonus.savog.api.configuration.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import site.jonus.savog.core.dao.UserDao
import site.jonus.savog.core.model.User

class ConvertToUserDetails(
    private val user: User,
    private val userDao: UserDao
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority>? {
        val roles = userDao.findRolesByEmail(user.email)?.map { it.role }
        val auth: MutableList<GrantedAuthority> = mutableListOf()
        roles?.forEach { auth.add(SimpleGrantedAuthority(it)) }
        return auth
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String? {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}