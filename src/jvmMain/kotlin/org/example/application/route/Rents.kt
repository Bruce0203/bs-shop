package org.example.application.route

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.example.application.authenticateUser
import org.example.application.model.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime


enum class RentTime(val displayName: String, val adder: (LocalDateTime) -> LocalDateTime) {
    TEN_MINUTES("10분", { it.plusMinutes(10) }),
    THIRTY_MINUTES("30분", { it.plusMinutes(30) }),
    ONE_HOUR("1시간", { it.plusHours(1) });
    override fun toString() = name
}

fun Routing.routeRents() {
    route("rent") {
        get("{id}") {
            try {
                call.authenticateUser()
                val id = (call.parameters["id"]!!.toInt())
                val rentTime = RentTime.valueOf(call.parameters["time"]!!)
                val existingRent = transaction {
                    Rent.find(Rents.place eq id).firstOrNull { it.expiredOn.isAfter(LocalDateTime.now()) }
                }
                if (existingRent !== null) {
                    call.respondRedirect("/")
                    throw Throwable()
                }
                transaction {
                    val place = Place[id]
                    val user = User[call.sessions.get<UserSession>()!!.id]
                    Rent.new {
                        this.place = place.id
                        this.author = user.id
                        createdOn = LocalDateTime.now()
                        expiredOn = LocalDateTime.now().let { rentTime.adder.invoke(it) }
                    }
                }
                call.respondRedirect("/")
            } catch (e: Throwable) {
                call.respondRedirect("/")
            }
        }
    }
}
