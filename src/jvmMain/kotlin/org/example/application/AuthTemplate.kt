package org.example.application

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.h1

fun HTML.login() {
    body {
        h1 { +"hello" }
    }
}