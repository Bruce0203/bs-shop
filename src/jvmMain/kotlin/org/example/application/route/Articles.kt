package org.example.application.route

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.example.application.UserSession
import org.example.application.model.Article
import org.example.application.model.Users
import org.example.application.template.article
import org.example.application.template.articleEditor
import org.example.application.template.articles
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun Routing.routeArticles() {
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