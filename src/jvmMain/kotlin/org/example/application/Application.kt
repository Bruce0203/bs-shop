package org.example.application

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

val dotenv = dotenv()

fun Application.mainModule() {
    routing {
        get("/") {
            call.respondRedirect("articles")
        }
        route("articles") {
            get {
                call.respondHtml { articles() }
            }
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
            get("{id}") {
                call.respondHtml { article(Article[call.request.uri.toInt()]) }
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
            get("style.css") {
                println("A")
                call.respondCss { styleCss() }
            }
        }
    }
}