package io.github.bruce0203.watchmore.entity

import java.io.Serializable

class SessionUser(
     var name: String,
     val email: String,
     val picture: String
) : Serializable {
     constructor(user: User) : this(
          name = user.name,
          email = user.email,
          picture = user.picture
     )
}