package io.github.bruce0203.watchmore.entity

import lombok.NoArgsConstructor
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.types.ObjectId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

@BsonDiscriminator("User")
@NoArgsConstructor
class User(
    val id: ObjectId = ObjectId(),
    @JvmField
    var name: String,
    var email: String,
    var picture: String,
    val role: String = "ROLE_USER",
)