package org.example.application

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() { initTestDB() }

fun startsServer() = embeddedServer(Netty, port = 8080) {
    mainModule()
}.start(wait = true)