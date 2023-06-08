package org.example.application

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.application.model.databaseModule

val dotenv = dotenv()

fun main() { startsServer() }

fun startsServer() = embeddedServer(Netty, port = dotenv.get("PORT").toInt()) {
    databaseModule()
    mainModule()

}.start(wait = true)