package org.example.application

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.application.model.databaseModule

fun main() { startsServer() }

fun startsServer() = embeddedServer(Netty, port = 80) {
    databaseModule()
    mainModule()

}.start(wait = true)