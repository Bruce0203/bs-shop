package io.github.bruce0203.watchmore.entity

class OAuthAttributes(
    var attributes: Map<String, Any> = mapOf(),
    var nameAttributeKey: String,
    var name: String,
    var email: String,
    var picture: String
) {

    // .. getter/setter 생략
    fun toEntity() = User(
        name = name,
        email = email,
        picture = picture
    )

    companion object {
        // 해당 로그인인 서비스가 kakao인지 google인지 구분하여, 알맞게 매핑을 해주도록 합니다.
        // 여기서 registrationId는 OAuth2 로그인을 처리한 서비스 명("google","kakao","naver"..)이 되고,
        // userNameAttributeName은 해당 서비스의 map의 키값이 되는 값이됩니다. {google="sub", kakao="id", naver="response"}
        fun of(registrationId: String, userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            if (registrationId == "kakao") {
                return ofKakao(userNameAttributeName, attributes)
            } else if (registrationId == "naver") {
                return ofNaver(userNameAttributeName, attributes)
            }
            return ofGoogle(userNameAttributeName, attributes)
        }

        private fun ofKakao(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            val kakao_account =
                attributes["kakao_account"] as Map<String, Any>? // 카카오로 받은 데이터에서 계정 정보가 담긴 kakao_account 값을 꺼낸다.
            val profile =
                kakao_account!!["profile"] as Map<String, Any>? // 마찬가지로 profile(nickname, image_url.. 등) 정보가 담긴 값을 꺼낸다.
            return OAuthAttributes(
                attributes,
                userNameAttributeName,
                profile!!["nickname"] as String,
                kakao_account["email"] as String,
                profile["profile_image_url"] as String
            )
        }

        private fun ofNaver(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            val response = attributes["response"] as Map<String, Any>? // 네이버에서 받은 데이터에서 프로필 정보다 담긴 response 값을 꺼낸다.
            return OAuthAttributes(
                attributes,
                userNameAttributeName,
                response!!["name"] as String,
                response["email"] as String,
                response["profile_image"] as String
            )
        }

        private fun ofGoogle(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                attributes,
                userNameAttributeName,
                attributes["name"] as String,
                attributes["email"] as String,
                attributes["picture"] as String
            )
        }
    }
}