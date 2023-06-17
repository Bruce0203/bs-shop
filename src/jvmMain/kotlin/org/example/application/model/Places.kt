package org.example.application.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Places : IntIdTable() {
    val name = varchar("name", 50)
    val contents = varchar("contents", 50)
    init { transaction { SchemaUtils.create(this@Places) } }
}

class Place(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Place>(Places)

    var name by Places.name
    var contents by Places.contents
}
