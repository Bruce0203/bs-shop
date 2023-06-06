package org.example.application

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.css.*
import kotlinx.html.*
import org.example.application.model.Article
import org.jetbrains.exposed.sql.transactions.transaction

internal fun HEAD.styleCss() {
    link { rel = "stylesheet"; href = "https://cdn.jsdelivr.net/npm/@picocss/pico@1/css/pico.min.css" }
    link { rel = "stylesheet"; href = "style.css" }
}

fun HTML.articles() {
    head {
        styleCss()
        title { +"게시글" }
    }
    body {
        h1 { +"게시글" }
        getForm(
            action = "/articles/new",
            encType = FormEncType.applicationXWwwFormUrlEncoded,
        ) {
            submitInput { value = "새 게시글 작성" }
        }
        transaction { Article.all().map { it } }.forEach { article ->
            article {
                h2 { +article.name }
                postForm(
                    action = "/articles/delete",
                ) {
                    hiddenInput(name = "id") { value = "${article.id}" }
                    postButton { +"삭제하기" }
                }
                p { +article.contents }
            }
        }
    }
}

fun HTML.articleEditor(article: Article? = null) {
    head { styleCss() }
    body {
        h1 { +"새 게시글" }
        form(
            action = "/articles",
            encType = FormEncType.multipartFormData,
            method = FormMethod.post
        ) {
            textInput(name= "name") {
                value = article?.name?: ""
                placeholder = "제목"
            }
            br
            textInput(name = "contents") {
                value = article?.contents?: ""
                placeholder = "내용"
            }
            submitInput {
                value = "게시"
                role = "button"
            }
        }
    }
}

fun HTML.article(article: Article) {
    head { styleCss() }
    body {
        h1 { +article.name }
        p { +article.contents }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

fun CssBuilder.styleCss() {
    //not implemented
}