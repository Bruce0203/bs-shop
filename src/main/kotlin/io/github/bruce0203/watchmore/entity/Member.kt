package io.github.bruce0203.watchmore.entity

import lombok.NoArgsConstructor
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.types.ObjectId

@BsonDiscriminator("Member")
@NoArgsConstructor
data class Member(
    val id: ObjectId = ObjectId(),
    val username: String,
    var password: String
)