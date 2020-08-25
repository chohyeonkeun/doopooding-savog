package com.savog.doopooding.core.model

import com.savog.doopooding.core.exposed.toJavaInstant
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant

object Managers : LongIdTable("manager", "id") {
    /**
     * 회원 ID
     */
    val memberId: Column<Long> = long("member_id")

    /**
     * 총괄 책임자
     */
    val executive: Column<Int> = integer("executive").default(0)

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

class Manager(id: EntityID<Long>) : LongEntity(id) {
    var memberId: Long by Managers.memberId

    var executive: Int by Managers.executive

    var deleted: Int by Managers.deleted

    var createdAt: Instant by Managers.createdAt

    var updatedAt: Instant by Managers.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<Manager>(Managers)
}