package org.example.application.route

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.example.application.authenticateUser
import org.example.application.model.*
import org.example.application.template.place
import org.example.application.template.placeEditor
import org.example.application.template.places
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.routePlaces() {
    route("places") {
        get {
            val user = call.sessions.get<UserSession>()
            call.respondHtml { places(user?.id?.let { transaction { User[it] } }) }
        }
        get("{id}") {
            val user = call.sessions.get<UserSession>()
            call.respondHtml { place(user?.id?.let { transaction { User[it] } }, transaction { Place[call.parameters["id"]!!.toInt()] }) }
        }
        post("{id}") {
            call.authenticateUser()
            call.receiveParameters().let { param ->
                transaction {
                    Place[call.parameters["id"]?.toInt()!!].apply {
                        param["name"]?.also { name = it }
                        param["contents"]?.also { contents = it }
                    }
                }
            }
            call.respondRedirect("/places")
        }
        get("new") {
            call.authenticateUser()
            val user = call.sessions.get<UserSession>()
            call.respondHtml { placeEditor(user?.id?.let { transaction { User[it].username } }) }
        }
        post {
            call.authenticateUser()
            call.receiveParameters().let { param ->
                transaction {
                    Place.new {
                        name = param["name"]!!
                        contents = param["contents"]!!
                    }
                }
            }
            call.respondRedirect("/places")
        }
        get("edit/{id}") {
            call.authenticateUser()
            val user = call.sessions.get<UserSession>()
            val place = transaction { Place[call.parameters["id"]!!.toInt()] }
            call.respondHtml { placeEditor(user?.id?.let { transaction { User[it].username } }, place) }
        }
        post("delete/{id}") {
            call.authenticateUser()
            transaction { Place[call.parameters["id"]!!.toInt()].delete() }
            call.respondRedirect("/places")
        }
    }
}