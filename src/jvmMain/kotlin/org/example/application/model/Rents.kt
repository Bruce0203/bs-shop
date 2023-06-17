package org.example.application.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object Rents: IntIdTable() {
    val place = reference("place", Places)
    val author = reference("user", Users)
    val createdOn = datetime("created_on")
    val expiredOn = datetime("expired_on")
    init { transaction { SchemaUtils.create(this@Rents) } }
}

class Rent(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Rent>(Rents)

    var place by Rents.place
    var author by Rents.author
    var createdOn by Rents.createdOn
    var expiredOn by Rents.expiredOn
}
