package com.savog.doopooding.core.model

import com.savog.doopooding.core.exposed.toJavaInstant
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant

object Categories : LongIdTable("category", "id") {
    /**
     * 카테고리 유형
     */
    val type: Column<String> = varchar("type", 50)

    /**
     * 카테고리명
     */
    var name: Column<String> = varchar("name", 50)

    /**
     * 생성자 ID
     */
    var creatorId: Column<String> = varchar("creator_id", 32)

    /**
     * 갱신자 ID
     */
    var updaterId: Column<String> = varchar("updater_id", 32)

    /**
     * 삭제 여부
     */
    var deleted: Column<Int> = integer("deleted").default(0)

    /**
     * 생성일
     */
    var createdAt: Column<Instant> = datetime("created_at").toJavaInstant()

    /**
     * 갱신일
     */
    var updatedAt: Column<Instant> = datetime("updated_at").toJavaInstant()
}

class Category(id: EntityID<Long>) : LongEntity(id) {
    var type: String by Categories.type

    var name: String by Categories.name

    var creatorId: String by Categories.creatorId

    var updaterId: String by Categories.updaterId

    var deleted: Int by Categories.deleted

    var createdAt: Instant by Categories.createdAt

    var updatedAt: Instant by Categories.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<Category>(Categories)
}