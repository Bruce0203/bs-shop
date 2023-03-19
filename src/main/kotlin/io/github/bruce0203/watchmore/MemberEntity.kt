package io.github.bruce0203.watchmore

import lombok.NoArgsConstructor
import org.bson.types.ObjectId

@NoArgsConstructor
data class MemberEntity(
    val id: ObjectId = ObjectId(),
    val username: String,
    var password: String
)