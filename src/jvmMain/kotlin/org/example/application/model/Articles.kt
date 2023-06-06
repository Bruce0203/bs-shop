package org.example.application.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Articles: IntIdTable() {
    val name = varchar("name", 50)
    val contents = varchar("contents", Int.MAX_VALUE)
    init { transaction { SchemaUtils.create(this@Articles) } }
}

class Article(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Article>(Articles)
    var name by Articles.name
    var contents by Articles.contents
}
