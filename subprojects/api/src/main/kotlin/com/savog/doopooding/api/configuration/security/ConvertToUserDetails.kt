package com.savog.doopooding.api.configuration.security

import com.savog.doopooding.core.dao.UserDao
import com.savog.doopooding.core.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

class ConvertToUserDetails(
    private val user: User,
    private val userDao: UserDao
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority>? {
        val roles = userDao.findRolesByEmail(user.email)?.map { it.role }
        return roles?.stream()?.map { SimpleGrantedAuthority(it) }?.collect(Collectors.toList())
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

    override fun getPassword(): String {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}