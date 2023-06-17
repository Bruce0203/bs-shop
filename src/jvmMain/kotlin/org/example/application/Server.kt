package org.example.application

import io.github.cdimascio.dotenv.dotenv
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.application.model.databaseModule
import org.slf4j.LoggerFactory
import java.io.File

val dotenv = dotenv()

fun main() { startsServer() }

fun startsServer() {
    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("ssl") {
            password = dotenv.get("SSL_PASSWORD")
            domains = listOf(dotenv.get("DOMAIN"))
        }
    }
    keyStore.saveToFile(keyStoreFile, dotenv.get("SSL_PASSWORD"))

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = dotenv.get("PORT").toInt()
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "ssl",
            keyStorePassword = { dotenv.get("SSL_PASSWORD").toCharArray() },
            privateKeyPassword = { dotenv.get("SSL_PASSWORD").toCharArray() }) {
            port = dotenv.get("SSL_PORT").toInt()
            keyStorePath = keyStoreFile
        }
        module {
            databaseModule()
            mainModule()
        }
    }
    embeddedServer(Netty, environment).start(wait = true)
}