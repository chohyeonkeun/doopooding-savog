package com.savog.doopooding.core.dao

import com.savog.doopooding.core.Codes
import com.savog.doopooding.core.Constants
import com.savog.doopooding.core.model.UserRole
import com.savog.doopooding.core.model.User
import com.savog.doopooding.core.model.UserRoles
import com.savog.doopooding.core.model.Users
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
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
        creatorId: String = Constants.SYSTEM_USERNAME
    ): List<ResultRow> {
        return UserRoles.batchInsert(roles) { role ->
            this[UserRoles.userId] = userId
            this[UserRoles.role] = role
            this[UserRoles.creatorId] = creatorId
            this[UserRoles.updaterId] = creatorId
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

    fun findRolesByEmail(email: String): List<UserRole>? {
        return UserRoles
            .join(Users, JoinType.LEFT, UserRoles.userId, Users.id)
            .select(Users.email eq email)
            .map { UserRole.wrapRow(it) }
    }
}