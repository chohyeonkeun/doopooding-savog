package com.savog.doopooding.core.model

import com.savog.doopooding.core.exposed.toJavaInstant
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant

object PetAttachments : LongIdTable("pet_attachment", "id") {
    /**
     * 애완동물 ID
     */
    val petId: Column<Long> = long("pet_id")

    /**
     * 첨부파일 유형
     */
    val type: Column<String> = varchar("type", 50)

    /**
     * S3 버킷
     */
    val bucket: Column<String> = varchar("bucket", 100)

    /**
     * S3
     */
    val key: Column<String> = varchar("key", 100)

    /**
     * 파일명
     */
    val filename: Column<String> = varchar("filename", 100)

    /**
     * 메모
     */
    val memo: Column<String?> = text("memo").nullable()

    /**
     * 생성자
     */
    val creatorId: Column<String> = varchar("creator_id", 30)

    /**
     * 수정자
     */
    val updaterId: Column<String> = varchar("updater_id", 30)

    /**
     * 삭제 여부
     */
    val deleted: Column<Int> = integer("deleted").default(0)

    /**
     * 생성일
     */
    val createdAt: Column<Instant> = datetime("created_at").toJavaInstant()

    /**
     * 수정일
     */
    val updatedAt: Column<Instant> = datetime("updated_at").toJavaInstant()
}

class PetAttachment(id: EntityID<Long>) : LongEntity(id) {
    var petId: Long by PetAttachments.petId

    var type: String by PetAttachments.type

    var bucket: String by PetAttachments.bucket

    var key: String by PetAttachments.key

    var filename: String by PetAttachments.filename

    var memo: String? by PetAttachments.memo

    var creatorId: String by PetAttachments.creatorId

    var updaterId: String by PetAttachments.updaterId

    var deleted: Int by PetAttachments.deleted

    var createdAt: Instant by PetAttachments.createdAt

    var updatedAt: Instant by PetAttachments.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<PetAttachment>(PetAttachments)
}