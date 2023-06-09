package org.example.application

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.example.application.model.Article
import org.example.application.model.User
import org.example.application.model.Users
import org.example.application.route.routeArticles
import org.example.application.route.routeAuth
import org.example.application.template.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes

fun Application.mainModule() {
    val oauth =
        OAuthServerSettings.OAuth2ServerSettings(
            name = "google",
            authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
            accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
            requestMethod = HttpMethod.Post,
            clientId = dotenv.get("GOOGLE_CLIENT_ID"),
            clientSecret = dotenv.get("GOOGLE_CLIENT_SECRET"),
            defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
            extraAuthParameters = listOf("access_type" to "offline"),
        )
    install(Sessions) {
        cookie<UserSession>("token", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAge = 10.minutes
            transform(SessionTransportTransformerEncrypt(
                hex(dotenv.get("secretEncryptKey")), hex(dotenv.get("secretSignKey"))
            ))
        }
    }
    install(CORS) {
        allowHeader("token")
        exposeHeader("token")
    }
    install(Authentication) {
        session<UserSession>() {
            validate { session ->
                if (transaction { User[session.id] }.toSession() == session) session else null
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }
    routing {
        get("style.css") { call.respondCss { styleCss() } }
        get("editor.js") { call.resolveResource("editor.js")?.also { call.respond(it) } }
        get { call.respondRedirect("articles") }
        routeArticles()
        routeAuth()
    }
}