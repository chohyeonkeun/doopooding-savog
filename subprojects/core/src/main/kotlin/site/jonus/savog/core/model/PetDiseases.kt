package site.jonus.savog.core.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column

object PetDiseases : LongIdTable("pet_disease", "id") {
    /**
     * 애완동물 ID
     */
    val petId: Column<Long> = long("pet_id")

    /**
     * 질병명
     */
    val name: Column<String> = varchar("name", 50)

    /**
     * 치료여부
     */
    val healed: Column<Int> = integer("healed").default(0)

    /**
     * 생성자
     */
    val creatorId: Column<String> = varchar("creator_id", 30)

    /**
     * 수정자
     */
    val updaterId: Column<String> = varchar("updater_id", 30)

    /**
     * 삭제여부
     */
    val deleted: Column<Int> = integer("deleted").default(0)
}

class PetDisease(id: EntityID<Long>) : LongEntity(id) {
    var petId: Long by PetDiseases.petId

    var name: String by PetDiseases.name

    var healed: Int by PetDiseases.healed

    var creatorId: String by PetDiseases.creatorId

    var updaterId: String by PetDiseases.updaterId

    var deleted: Int by PetDiseases.deleted

    fun getId(): Long = this.id.value
    companion object : LongEntityClass<PetDisease>(PetDiseases)
}