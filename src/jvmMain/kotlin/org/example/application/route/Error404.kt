package org.example.application.route

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import org.example.application.template.styleCss

fun Routing.route404() {
    get("/{path}") {
        call.respondHtml {
            head { styleCss() }
            body {
                div("center") {
                    h2 { +"Error 404" }
                    pre { p { +"없는 페이지 입니다" } }
                    form(action = "/") {
                        getButton { +"돌아가기" }
                    }
                }
            }
        }

    }
}