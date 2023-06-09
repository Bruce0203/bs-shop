package org.example.application.template

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.css.*
import kotlinx.css.Float
import kotlinx.css.properties.TextDecoration
import kotlinx.html.HEAD
import kotlinx.html.link
import kotlinx.html.meta

fun CssBuilder.styleCss() {
    rule(".auto-width") { width = LinearDimension.auto }
    rule(".inline") { display = Display.inline }
    rule(".inline-block") { display = Display.inlineBlock }
    rule(".vertical") { verticalAlign = VerticalAlign.sub }
    rule(".vertical-middle") { verticalAlign = VerticalAlign.middle }
    rule(".right") { float = Float.right }
    rule(".left") { float = Float.left }
    rule(".relative") { position = Position.relative }
    rule(".absolute") { position = Position.absolute }
    rule(".flex") { display = Display.flex }
    rule(".width") { width = 100.pct }
    rule(".height") {
        width = 100.pct
        paddingBottom = 30.rem
    }
    rule(".text-right-top") {
        verticalAlign = VerticalAlign.top
        textAlign = TextAlign.initial
    }
    rule(".no-text-color") {
        color = Color.inherit
        textDecoration = TextDecoration.none
    }
    rule(".gap") {
        paddingLeft = 2.pct
        paddingRight = 2.pct
    }
    rule(".center") {
        textAlign = TextAlign.center
        alignContent = Align.center
        alignItems = Align.center
        alignSelf = Align.center
    }
    rule(".block") {
        display = Display.block
    }
    rule(".no-top") {
        marginTop = (-4).px
        boxSizing = BoxSizing.borderBox
    }
    rule("text-align-right") {
        textAlign = TextAlign.end
    }
    rule("word") {
        wordWrap = WordWrap.breakWord
        wordBreak = WordBreak.breakWord
    }

}

internal fun HEAD.styleCss() {
    link { rel = "stylesheet"; href = "https://cdn.jsdelivr.net/npm/water.css@2/out/water.css" }
    link { rel = "stylesheet"; href = "/style.css" }
    meta { name = "viewport"; content = "width=device-width, initial-scale=1.0" }
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

