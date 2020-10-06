package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object BoardComments : LongIdTable("board_comment", "id") {
    val boardId: Column<Long> = long("board_id")

    val userId: Column<Long> = long("user_id")

    val parentId: Column<Long?> = long("parent_id").nullable()

    val comment: Column<String> = text("comment")

    val showOnTop: Column<Int> = integer("show_on_top").default(0)

    val deleted: Column<Int> = integer("deleted").default(0)

    val createdAt: Column<Instant> = datetime("created_at").toJavaInstant()

    val updatedAt: Column<Instant> = datetime("updated_at").toJavaInstant()
}

class BoardComment(id: EntityID<Long>) : LongEntity(id) {
    var boardId: Long by BoardComments.boardId

    var userId: Long by BoardComments.userId

    var parentId: Long? by BoardComments.parentId

    var comment: String by BoardComments.comment

    var showOnTop: Int by BoardComments.showOnTop

    var deleted: Int by BoardComments.deleted

    var createdAt: Instant by BoardComments.createdAt

    var updatedAt: Instant by BoardComments.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<BoardAttachment>(BoardAttachments)
}
