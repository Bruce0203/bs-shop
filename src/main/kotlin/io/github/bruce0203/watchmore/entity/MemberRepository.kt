package io.github.bruce0203.watchmore.entity

import io.github.bruce0203.watchmore.entity.Member
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MemberRepository : MongoRepository<Member, ObjectId> {
    fun findByUsername(username: String): Member?
}