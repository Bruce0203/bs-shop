package org.example.application

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() { startsServer() }

fun startsServer() = embeddedServer(Netty, port = 8080) {
    databaseModule()
    mainModule()

}.start(wait = true)