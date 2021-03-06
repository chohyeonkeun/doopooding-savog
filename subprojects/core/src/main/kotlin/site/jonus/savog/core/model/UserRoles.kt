package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object UserRoles : LongIdTable("user_role", "id") {
    /**
     * 회원ID
     */
    val userId: Column<Long> = long("user_id")

    /**
     * 권한
     */
    val role: Column<String> = varchar("role", 30)

    /**
     * 삭제 여부
     */
    val deleted: Column<Int> = integer("deleted").default(0)

    /**
     * 생성일
     */
    val createdAt: Column<Instant> = datetime("created_at").toJavaInstant()

    /**
     * 갱신일
     */
    val updatedAt: Column<Instant> = datetime("updated_at").toJavaInstant()
}

class UserRole(id: EntityID<Long>) : LongEntity(id) {
    var userId: Long by UserRoles.userId

    var role: String by UserRoles.role

    var deleted: Int by UserRoles.deleted

    val createdAt: Instant by UserRoles.createdAt

    val updatedAt: Instant by UserRoles.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<UserRole>(UserRoles)
}