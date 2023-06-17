package org.example.application.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.application.dotenv
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Users: IntIdTable() {
    val username = varchar("username", 16)
    val studentId = varchar("studentId", 5)
    val email = varchar("email", 320)
    init { transaction { SchemaUtils.create(this@Users) } }
}
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var username by Users.username
    var studentId by Users.studentId
    var email by Users.email
}

@Serializable
data class UserSession(val id: Int)

@Serializable
data class UserInfo(
    val id: String,
    val email: String,
    @SerialName("verified_email") val verifiedEmail: Boolean,
    val name: String,
    @SerialName("given_name") val username: String,
    @SerialName("family_name") val studentId: String,
    val picture: String,
    val locale: String,
    val hd: String? = null
)

val User.isAdmin get() = dotenv.get("ADMINS").split(", ").contains(email)
val User.displayName get() = "${studentId.studentIdToGrade} $username"
val String.studentIdToGrade get() = "${this[0]}학년 ${substring(1, 3).toInt()}반 ${substring(3, 5).toInt()}번"