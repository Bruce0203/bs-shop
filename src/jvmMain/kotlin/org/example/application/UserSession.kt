package org.example.application

import io.ktor.server.auth.*
import org.example.application.model.User

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
