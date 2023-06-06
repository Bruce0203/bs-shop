package org.example.application.model

import org.jetbrains.exposed.sql.Database

fun connect() = Database.connect("jdbc:h2:file:./articles", driver = "org.h2.Driver")

fun databaseModule() {
    connect()
}
