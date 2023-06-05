package org.example.application

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionScope
import java.util.concurrent.Executors
import kotlin.concurrent.thread

fun connect() = Database.connect("jdbc:h2:file:./articles", driver = "org.h2.Driver")

fun databaseModule() {
    connect()
    transaction { SchemaUtils.create(Articles) }
}
