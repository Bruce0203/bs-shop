package org.example.application.template

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.html.*

suspend fun ApplicationCall.login() = respondHtml {
    head { styleCss() }
    body {
        div("center block") {
            h1("center block") { +"로그인" }
            postForm(
                classes = "center",
                action = "/login",
            ) {
                div {
                    textInput(classes = "center inline-block", name = "username") {
                        placeholder = "사용자 이름"
                        required = true
                    }
                    br("block")
                    passwordInput(classes = "center inline-block", name = "password") {
                        placeholder = "비밀번호"
                        required = true
                    }
                }

                if (parameters["error"] !== null) {
                    pre { cite { +"아이디 혹은 비밀번호가 일치하지 않습니다" } }
                } else br
                a("/signup", classes = "inline-block vertical gap") { +"계정이 없으신가요?" }
                submitInput(classes = "inline-block vertical") {
                    value = "로그인"
                    role = "button"
                }
            }
        }
    }
}

suspend fun ApplicationCall.signup() = respondHtml {
    head { styleCss() }
    body {
        div("center") {
            h1 { +"회원가입" }
            postForm(
                classes = "center",
                action = "/signup",
            ) {
                div {
                    textInput(classes = "center inline-block", name = "username") {
                        placeholder = "사용자 이름"
                        required = true
                    }
                    br
                    passwordInput(classes = "center inline-block", name = "password") {
                        placeholder = "비밀번호"
                        required = true
                    }
                }
                pre {
                    cite {
                        when (parameters["error"]?.toInt()?.let { HttpStatusCode.fromValue(it) }) {
                            HttpStatusCode.BadRequest -> {
                                +"아이디와 비밀번호의 길이는 3자 이상 15자 이하여야 합니다"
                                +"아이디는 알파벳 소문자, 숫자, '-', '_', '.' 문자만 사용할 수 있습니다"
                            }
                            HttpStatusCode.Conflict -> { +"이미 사용중인 아이디입니다. 다른 아이디를 입력해주세요" }
                            else -> br
                        }
                    }
                }
                a("/login", classes = "inline-block vertical gap") {
                    +"로그인"
                }
                submitInput(classes = "inline-block vertical") {
                    value = "회원가입"
                    role = "button"
                }

            }
        }

    }
}