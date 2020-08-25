package com.savog.doopooding.core.model

import com.savog.doopooding.core.exposed.toJavaInstant
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant

object Members : LongIdTable("member", "id") {
    /**
     * 회원 아이디
     */
    val userId: Column<String> = varchar("user_id", 30)

    /**
     * 비밀번호
     */
    val password: Column<String> = varchar("password", 30)

    /**
     * 닉네임
     */
    var nickname: Column<String> = varchar("nickname", 30)

    /**
     * 회원명
     */
    val name: Column<String> = varchar("name", 30)

    /**
     * 이메일 주소
     */
    val email: Column<String> = varchar("email", 45)

    /**
     * 연락처
     */
    val phone: Column<String> = varchar("phone", 45)

    /**
     * 로그인 유형
     */
    val type: Column<String> = varchar("type", 30)

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

class Member(id: EntityID<Long>) : LongEntity(id) {
    var userId: String by Members.userId

    var password: String by Members.password

    var nickname: String by Members.nickname

    var name: String by Members.name

    var email: String by Members.email

    var phone: String by Members.phone

    var type: String by Members.type

    var deleted: Int by Members.deleted

    var createdAt: Instant by Members.createdAt

    var updatedAt: Instant by Members.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<Member>(Members)
}