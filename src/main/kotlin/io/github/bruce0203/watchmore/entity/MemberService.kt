package io.github.bruce0203.watchmore.entity

import lombok.AllArgsConstructor
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.ArrayList


@Service
@AllArgsConstructor
class MemberService : UserDetailsService {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Transactional
    fun joinUser(member: Member): ObjectId {
        val passwordEncoder = BCryptPasswordEncoder()
        member.password = passwordEncoder.encode(member.password)
        return memberRepository.save(member).id
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val userEntity = memberRepository.findByUsername(username)
        if (userEntity === null) throw UsernameNotFoundException(username)
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        if ("admin" == username) {
            authorities.add(SimpleGrantedAuthority(Role.ADMIN.value))
        } else {
            authorities.add(SimpleGrantedAuthority(Role.MEMBER.value))
        }
        return User(userEntity.username, userEntity.password, authorities)
    }

}
