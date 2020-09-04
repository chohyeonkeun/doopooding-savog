package com.savog.doopooding.core.dao

import com.savog.doopooding.core.Codes
import com.savog.doopooding.core.model.Role
import com.savog.doopooding.core.model.Roles
import com.savog.doopooding.core.model.User
import com.savog.doopooding.core.model.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class UserDao : BaseDao() {

    fun create(
        email: String,
        password: String,
        nickname: String,
        type: String = Codes.LoginType.EMAIL.value
    ): Long {
        return Users.insertAndGetId { stmt ->
            stmt[this.email] = email
            stmt[this.password] = password
            stmt[this.nickname] = nickname
            stmt[this.type] = type
        }.value
    }

    fun batchCreateRolesByUserId(
        userId: Long,
        roles: List<String>,
        creatorId: String = "system"
    ): List<ResultRow> {
        return Roles.batchInsert(roles) { role ->
            this[Roles.userId] = userId
            this[Roles.role] = role
            this[Roles.creatorId] = creatorId
            this[Roles.updaterId] = creatorId
        }
    }

    fun update(
        targetId: Long,
        password: String? = null,
        nickname: String? = null
    ): Int {
        return Users.update({ Users.id eq targetId }) { stmt ->
            password?.let { stmt[this.password] = it }
            nickname?.let { stmt[this.nickname] = it }
        }
    }

    fun findByEmail(email: String): User? {
        return Users
            .select(Users.email eq email)
            .map { User.wrapRow(it) }
            .firstOrNull()
    }

    fun findRolesByEmail(email: String): List<Role>? {
        return Roles
            .join(Users, JoinType.LEFT, Roles.userId, Users.id)
            .select(Users.email eq email)
            .map { Role.wrapRow(it) }
    }
}