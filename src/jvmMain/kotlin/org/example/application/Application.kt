package org.example.application

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.example.application.model.User
import org.example.application.model.UserInfo
import org.example.application.model.UserSession
import org.example.application.model.Users
import org.example.application.route.route404
import org.example.application.route.routePlaces
import org.example.application.route.routeAuth
import org.example.application.route.routeRents
import org.example.application.template.respondCss
import org.example.application.template.styleCss
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.minutes

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

val redirects = mutableMapOf<String, String>()

fun Application.mainModule() {
    val oauth =
        OAuthServerSettings.OAuth2ServerSettings(
            name = "google",
            authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
            accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
            requestMethod = HttpMethod.Post,
            clientId = dotenv.get("GOOGLE_CLIENT_ID"),
            clientSecret = dotenv.get("GOOGLE_CLIENT_SECRET"),
            defaultScopes = listOf(
                "https://www.googleapis.com/auth/userinfo.profile",
                "https://www.googleapis.com/auth/userinfo.email",
            ),
            extraAuthParameters = listOf("access_type" to "offline"),
            onStateCreated = { call, state ->
                redirects[state] = call.request.queryParameters["redirectUrl"]!!
            }
        )

    install(Sessions) {
        cookie<AuthSession>("authSession", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAge = 10.minutes
            transform(SessionTransportTransformerEncrypt(
                hex(dotenv.get("secretEncryptKey")), hex(dotenv.get("secretSignKey"))
            ))
        }
        cookie<UserSession>("userSession", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAge = 10.minutes
            transform(SessionTransportTransformerEncrypt(
                hex(dotenv.get("secretEncryptKey")), hex(dotenv.get("secretSignKey"))
            ))
        }
    }
    install(Authentication) {
        oauth {
            urlProvider = { dotenv.get("CALLBACK") }
            providerLookup = { oauth }
            client = httpClient
        }
    }
    routing {
        get("style.css") { call.respondCss { styleCss() } }
        get("editor.js") { call.resolveResource("editor.js")?.also { call.respond(it) } }
        get { call.respondRedirect("places") }
        routePlaces()
        route404()
        routeAuth()
        routeRents()
    }
}

data class AuthSession(val state: String, val token: String)

suspend fun AuthSession.getUserInfo(): UserInfo {
    val userInfo: UserInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
        headers {
            append(HttpHeaders.Authorization, "Bearer $token")
        }
    }.body()
    return userInfo
}

suspend fun ApplicationCall.authenticateUser() {
    val authSession: AuthSession? = sessions.get()
    val userInfo = authSession?.getUserInfo()
    if (userInfo != null) {
        val user = transaction {
            User.find(Users.email eq userInfo.email).firstOrNull()
                ?.apply {
                    username = userInfo.username
                }
                ?: User.new {
                    username = userInfo.username
                    email = userInfo.email
                    studentId = userInfo.studentId
                }
        }
        sessions.set(UserSession(user.id.value))

    } else {
        respondRedirect("/login?redirectUrl=${request.uri}")
        throw Throwable()
    }
}