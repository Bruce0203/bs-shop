package org.example.application

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.body
import kotlinx.css.h1
import kotlinx.css.script
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.script
import org.example.application.model.Article
import org.example.application.model.Session
import org.example.application.model.Sessions
import org.example.application.model.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

val dotenv = dotenv()

fun Application.mainModule() {
    install(Authentication) {
        bearer {
            realm = "Access to the '/' path"
            authenticate { credential ->
                transaction {
                    Session.find { Sessions.token eq credential.token }.firstOrNull()
                        ?.user?.let { User[it] }
                        ?.run { UserIdPrincipal(username) }
                }
            }
        }
    }
    routing {
        get("app.js") {
            call.resolveResource("app.js")?.let { call.respond(it) }
        }
        get {
            call.resolveResource("index.html")?.let { call.respond(it) }
        }
        get("/login") {
            call.respondHtml { login() }
        }
        route("articles") {
            get {
                call.respondHtml { articles() }
            }
            get("{id}") {
                call.respondHtml { article(Article[call.request.uri.toInt()]) }
            }
            authenticate {
                get("new") {
                    call.respondHtml { articleEditor() }
                }
                post {
                    call.receiveParameters().let { param ->
                        transaction {
                            Article.new {
                                name = param["name"]!!
                                contents = param["contents"]!!
                            }
                        }
                    }
                    call.respondRedirect("/articles")
                }
                get("{id}/edit") {
                    call.respondHtml { articleEditor(Article[call.request.uri.toInt()]) }
                }
                post("/delete") {
                    call.receiveParameters().let { param ->
                        transaction { param["id"]?.also { Article[it.toInt()].delete() } }
                    }
                    call.respondRedirect("/articles")
                }
                post("{id}") {
                    call.receiveParameters().let { param ->
                        transaction {
                            Article[call.request.uri.toInt()].apply {
                                param["name"]?.also { name = it }
                                param["contents"]?.also { contents = it }
                            }
                        }
                    }
                    call.respondRedirect("/articles")
                }
            }
            get("style.css") {
                println("A")
                call.respondCss { styleCss() }
            }
        }
    }
}