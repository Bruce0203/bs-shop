package io.github.bruce0203.watchmore.entity

import io.github.bruce0203.watchmore.entity.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : MongoRepository<User, Long> {
    fun findByEmail(email: String?): Optional<User>
}