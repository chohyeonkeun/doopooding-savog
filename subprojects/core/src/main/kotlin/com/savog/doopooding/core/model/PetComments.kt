package com.savog.doopooding.core.model

import com.savog.doopooding.core.exposed.toJavaInstant
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import java.time.Instant

object PetComments : LongIdTable("pet_comment", "id") {
    val petId: Column<Long> = long("pet_id")

    val userId: Column<Long> = long("user_id")

    val parentId: Column<Long?> = long("parent_id").nullable()

    val comment: Column<String> = text("comment")

    val showOnTop: Column<Int> = integer("show_on_top").default(0)

    val deleted: Column<Int> = integer("deleted").default(0)

    val createdAt: Column<Instant> = datetime("created_at").toJavaInstant()

    val updatedAt: Column<Instant> = datetime("updated_at").toJavaInstant()
}

class PetComment(id: EntityID<Long>) : LongEntity(id) {
    var petId: Long by PetComments.petId

    var userId: Long by PetComments.userId

    var parentId: Long? by PetComments.parentId

    var comment: String by PetComments.comment

    var showOnTop: Int by PetComments.showOnTop

    var deleted: Int by PetComments.deleted

    var createdAt: Instant by PetComments.createdAt

    var updatedAt: Instant by PetComments.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<PetComment>(PetComments)
}
