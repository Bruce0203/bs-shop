package org.example.application.route

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.form
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import org.example.application.*
import org.example.application.template.styleCss

fun Routing.routeAuth() {
    authenticate {
        get("/login") {}
        get("/callback") {
            try {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                val emailIP = dotenv.get("EMAIL_IP")
                val authSession = AuthSession(principal!!.state!!, principal.accessToken)
                val userInfo = authSession.getUserInfo()
                if (userInfo.email.endsWith(emailIP)) {
                    call.sessions.set(authSession)
                    call.authenticateUser()
                    val redirect = redirects[principal.state!!]
                    call.respondRedirect(redirect!!)
                } else {
                    call.respondHtml {
                        head { styleCss() }
                        body {
                            div("center") {
                                pre { p { +"백신고등학교 핵생용 이매일로만 로그인 할 수 있습니다" } }
                                form(action = "/logout") {
                                    getButton { +"다시 로그인하기" }
                                }
                            }
                        }
                    }
                    throw Throwable()
                }

            } catch(e: Throwable) {
                e.printStackTrace()
            }
        }
    }
    get("/logout") {
        call.sessions.clear("userSession")
        call.sessions.clear("authSession")
        call.respondRedirect("/")
    }
}