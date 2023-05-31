package io.github.bruce0203.watchmore.handler

import io.github.bruce0203.watchmore.entity.CustomOAuth2UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Autowired
    lateinit var userService: CustomOAuth2UserService

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/", "/home", "/signup").permitAll()
                    .anyRequest().authenticated()
            }
        http
            .logout()
            .logoutSuccessUrl("/") // 로그아웃에 대해서 성공하면 "/"로 이동
            .and()
            .oauth2Login()
            .defaultSuccessUrl("/hello")
            .userInfoEndpoint()
            .userService(userService) // oauth2 로그인에 성공하면, 유저 데이터를 가지고 우리가 생성한

        // customOAuth2UserService에서 처리를 하겠다. 그리고 "/login-success"로 이동하라.
        return http.build()
    }

}