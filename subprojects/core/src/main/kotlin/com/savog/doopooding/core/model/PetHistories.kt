package com.savog.doopooding.core.model

import com.savog.doopooding.core.exposed.toJavaInstant
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant

object PetHistories : LongIdTable("pet_history", "id") {
    /**
     * 애완동물 ID
     */
    val petId: Column<Long> = long("pet_id")

    /**
     * 카테고리 ID
     */
    val categoryId: Column<Long?> = long("category_id").nullable()

    /**
     * 관리자 ID
     */
    val managerId: Column<Long> = long("manager_id")

    /**
     * 히스토리 유형
     */
    val contentType: Column<String> = varchar("content_type", 50)

    /**
     * 변경내용
     */
    val content: Column<String> = varchar("content", 45)

    /**
     * 생성자 ID
     */
    val creatorId: Column<String> = varchar("creator_id", 32)

    /**
     * 갱신자 ID
     */
    val updaterId: Column<String> = varchar("updater_id", 32)

    /**
     * 공지 여부
     */
    val showOnTop: Column<Int> = integer("show_on_top").default(0)

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

class PetHistory(id: EntityID<Long>) : LongEntity(id) {
    var petId: Long by PetHistories.petId

    var categoryId: Long? by PetHistories.categoryId

    var managerId: Long by PetHistories.managerId

    var contentType: String by PetHistories.contentType

    var content: String by PetHistories.content

    var creatorId: String by PetHistories.creatorId

    var updaterId: String by PetHistories.updaterId

    var showOnTop: Int by PetHistories.showOnTop

    var deleted: Int by PetHistories.deleted

    var createdAt: Instant by PetHistories.createdAt

    var updatedAt: Instant by PetHistories.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<PetHistory>(PetHistories)
}