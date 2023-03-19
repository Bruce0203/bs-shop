package io.github.bruce0203.watchmore

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .authorizeHttpRequests(
                Customizer { requests ->
                    requests
                        .requestMatchers("/", "/home").permitAll()
                        .anyRequest().authenticated()
                }
            )
            .formLogin { form: FormLoginConfigurer<HttpSecurity?> ->
                form
                    .loginPage("/login")
                    .permitAll()
            }
            .logout { logout: LogoutConfigurer<HttpSecurity?> -> logout.permitAll() }
        return http.build()
    }


//    @Throws(java.lang.Exception::class)
//    fun configure(auth: AuthenticationManagerBuilder) {
//        auth.userDetailsService<UserDetailsService>(memberService).passwordEncoder(passwordEncoder())
//    }

    @Bean
    fun userDetailsService(): UserDetailsService? {
        val user: UserDetails = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }

}