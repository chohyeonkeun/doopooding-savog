package site.jonus.savog.core.dao

import site.jonus.savog.core.Codes
import site.jonus.savog.core.Constants
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import site.jonus.savog.core.model.User
import site.jonus.savog.core.model.UserRole
import site.jonus.savog.core.model.UserRoles
import site.jonus.savog.core.model.Users

@Repository
@Transactional
class UserDao : BaseDao() {

    fun create(
        name: String,
        email: String,
        password: String? = null,
        nickname: String,
        loginType: String = Codes.LoginType.EMAIL.value
    ): Long {
        return Users.insertAndGetId { stmt ->
            stmt[this.name] = name
            stmt[this.email] = email
            password?.let { stmt[this.password] = it }
            stmt[this.nickname] = nickname
            stmt[this.loginType] = loginType
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