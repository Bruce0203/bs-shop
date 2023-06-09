package org.example.application.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.example.application.model.User
import org.example.application.model.Users
import org.example.application.template.login
import org.example.application.template.signup
import org.example.application.toSession
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.routeAuth() {
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

}