package org.example.application

import io.github.cdimascio.dotenv.dotenv
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
import org.example.application.template.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.time.Duration.Companion.minutes

val dotenv = dotenv()

data class UserSession(val id: Int, val name: String, val password: String) : Principal {
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + id
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserSession

        if (name != other.name) return false
        if (password != other.password) return false
        if (id != other.id) return false

        return true
    }
}
fun User.toSession() = UserSession(id.value, username, password)

fun Application.mainModule() {
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
        get("logout") {
            call.sessions.clear("token")
            call.respondRedirect("/")
        }
        route("login") {
            get { call.login() }
            post {
                    val user = call.receiveParameters().let { param ->
                        UserPasswordCredential(
                            param["username"] ?: throw Throwable(),
                            param["password"] ?: throw Throwable()
                        )
                    }.run {
                        transaction {
                            User.find((Users.username eq name) and (Users.password eq password)).firstOrNull()
                        }
                    }?: throw Throwable().also { call.respondRedirect("/login?error") }
                    call.sessions.set(user.toSession())
                    call.respondRedirect("/")

            }
        }
        route("signup") {
            get { call.signup() }
            post {
                val user = call.receiveParameters().let { param ->
                    UserPasswordCredential(
                        param["username"] ?: throw Throwable(),
                        param["password"] ?: throw Throwable()
                    ).also {
                        if (
                            !(3..15).contains(it.name.length)
                            || !(3..15).contains(it.password.length)
                            || it.name.contains(" ")
                            || it.password.contains(" ")
                            || !Regex("^[a-zA-Z0-9](_(?!(\\.|_))|\\.(?!(_|\\.))|[a-zA-Z0-9]){6,18}[a-zA-Z0-9]\$")
                                .containsMatchIn(it.name)
                        ) {
                            throw Throwable().also {
                                call.respondRedirect("/signup?error=${HttpStatusCode.BadRequest.value}")
                            }
                        }
                    }
                }.let { user ->
                    if (transaction { User.find(Users.username eq user.name).firstOrNull() } !== null) {
                        call.respondRedirect("/signup?error=${HttpStatusCode.Conflict.value}")
                        throw Throwable()
                    }
                    transaction {
                        User.new {
                            username = user.name
                            password = user.password
                        }
                    }
                }
                call.sessions.set(user.toSession())
                call.respondRedirect("/")
            }
        }
        get { call.respondRedirect("articles") }
        route("articles") {
            get { call.respondHtml { articles(); } }
            get("{id}") {
                val user = call.sessions.get<UserSession>()?.id
                call.respondHtml { article(user, transaction { Article[call.parameters["id"]!!.toInt()] }) }
            }
            authenticate {
                get("new") {
                    val user = call.sessions.get<UserSession>()
                    call.respondHtml { articleEditor(user?.name) }
                }
                post {
                    call.receiveParameters().let { param ->
                        transaction {
                            Article.new {
                                name = param["name"]!!
                                contents = param["contents"]!!
                                author = EntityID(call.sessions.get<UserSession>()!!.id, Users)
                                created = LocalDate.now()
                            }
                        }
                    }
                    call.respondRedirect("/articles")
                }
                get("edit/{id}") {
                    val user = call.sessions.get<UserSession>()
                    val article = transaction { Article[call.parameters["id"]!!.toInt()] }
                    call.respondHtml { articleEditor(user?.name, article) }
                }
                post("delete/{id}") {
                    transaction { Article[call.parameters["id"]!!.toInt()].delete() }
                    call.respondRedirect("/articles")
                }
                post("{id}") {
                    call.receiveParameters().let { param ->
                        transaction {
                            Article[call.parameters["id"]?.toInt()!!].apply {
                                param["name"]?.also { name = it }
                                param["contents"]?.also { contents = it }
                            }
                        }
                    }
                    call.respondRedirect("/articles")
                }
            }
        }
    }
}