package org.example.application

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val dotenv = dotenv()

fun Application.mainModule() {
    install(Authentication) {
        bearer {
            realm = "Access to the '/' path"
            authenticate { tokenCredential ->
                if (tokenCredential.token == "abc123") {
                    UserIdPrincipal("jetbrains")
                } else null
            }
        }
    }
    routing {
        authenticate {
            get("/") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
    }

}