package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object Users : LongIdTable("user", "id") {
    /**
     * 회원명
     */
    val name: Column<String> = varchar("name", 45)

    /**
     * 이메일 주소
     */
    val email: Column<String> = varchar("email", 45)

    /**
     * 비밀번호
     */
    val password: Column<String?> = varchar("password", 100).nullable()

    /**
     * 닉네임
     */
    var nickname: Column<String> = varchar("nickname", 30)

    /**
     * 로그인 유형
     */
    val loginType: Column<String> = varchar("login_type", 30)

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

class User(id: EntityID<Long>) : LongEntity(id) {
    var name: String by Users.name

    var email: String by Users.email

    var password: String? by Users.password

    var nickname: String by Users.nickname

    var loginType: String by Users.loginType

    var deleted: Int by Users.deleted

    var createdAt: Instant by Users.createdAt

    var updatedAt: Instant by Users.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<User>(Users)
}