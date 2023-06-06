package org.example.application.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction

object Sessions: IntIdTable() {
    val user = reference("user", Users)
    val token = varchar("token", 36)
    val created = datetime("created")
    init { transaction { SchemaUtils.create(this@Sessions) } }
}

class Session(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Session>(Sessions)
    var user by Sessions.user
    var token by Sessions.token
}
