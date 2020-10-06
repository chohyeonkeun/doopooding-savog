package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import site.jonus.savog.core.exposed.toJavaInstant
import java.time.Instant

object Boards : LongIdTable("board", "id") {
    val userId: Column<Long> = long("user_id")

    val title: Column<String> = varchar("title", 45)

    val content: Column<String?> = text("content").nullable()

    val type: Column<String> = varchar("type", 30)

    val creatorId: Column<String> = varchar("creator_id", 30)

    val updaterId: Column<String> = varchar("updater_id", 30)

    val showOnTop: Column<Int> = integer("show_on_top").default(0)

    val deleted: Column<Int> = integer("deleted").default(0)

    val createdAt: Column<Instant> = datetime("created_at").toJavaInstant()

    val updatedAt: Column<Instant> = datetime("updated_at").toJavaInstant()
}

class Board(id: EntityID<Long>) : LongEntity(id) {
    var userId: Long by Boards.userId

    var title: String by Boards.title

    var content: String? by Boards.content

    var type: String by Boards.type

    var creatorId: String by Boards.creatorId

    var updaterId: String by Boards.updaterId

    var showOnTop: Int by Boards.showOnTop

    var deleted: Int by Boards.deleted

    var createdAt: Instant by Boards.createdAt

    var updatedAt: Instant by Boards.updatedAt

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<Board>(Boards)
}