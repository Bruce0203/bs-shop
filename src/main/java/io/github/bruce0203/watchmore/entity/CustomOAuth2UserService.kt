package io.github.bruce0203.watchmore.entity

import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private val userRepository: UserRepository? = null

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2UserService = DefaultOAuth2UserService()
        val oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest)
        val registrationId = oAuth2UserRequest.clientRegistration.registrationId
        val userNameAttributeName =
            oAuth2UserRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        val attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.attributes)
        val user = saveOrUpdate(attributes)
        println(attributes.attributes)
        return DefaultOAuth2User(
            setOf(SimpleGrantedAuthority(user.role)), attributes.attributes, attributes.nameAttributeKey
        )
    }

    private fun saveOrUpdate(attributes: OAuthAttributes): User {
        val user: User = userRepository!!.findByEmail(attributes.email).orElseGet { attributes.toEntity() }
        return userRepository.save(user)
    }
}