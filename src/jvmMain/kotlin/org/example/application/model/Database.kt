package org.example.application.model

import org.jetbrains.exposed.sql.Database

fun databaseModule() {
    connect("app")
}

fun connect(db: String) = Database.connect("jdbc:h2:file:./$db", driver = "org.h2.Driver")
