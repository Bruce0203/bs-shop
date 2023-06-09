package org.example.application.template

import kotlinx.html.*
import org.example.application.model.Article
import org.example.application.model.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

const val serviceName = "그냥 게시판"

fun HTML.articles() {
    head {
        styleCss()
        title { +serviceName }
    }
    body {
        h1 { +serviceName }
        p { +"님도 여기에 아무 글을 써보세요!" }
        div {
            getForm(action = "/articles/new") {
                getButton { +"글쓰기" }
            }
        }
        br
        transaction { Article.all().map { it } }.forEach { article ->
            a("/articles/${article.id}", classes = "no-text-color") {
                article("no-text-color") {
                    h2(classes = "no-top vertical inline-block") { +article.name }
                    pre(classes = "no-top right vertical inline-block") {
                        cite {
                            +article.created.format(DateTimeFormatter.ofPattern("YYYY.MM.dd"))
                            +"\n"
                            +transaction { User[article.author].username }
                        }
                    }
                    br
                    p("no-text-color inline-block") { +article.contents }
                    br;br;
                }
            }
        }
        footer {}
    }
}

fun HTML.articleEditor(username: String?, article: Article? = null) {
    head { styleCss() }
    body {
        h1 { +"글쓰기" }
        postForm(action = if (article === null) "/articles" else "/articles/${article.id}") {
            name = "editor"
            textInput(classes = "inline-block vertical", name = "name") {
                value = article?.name ?: username?.let { "${it}님의 글" } ?: ""
                placeholder = "제목"
                required = true
            }
            postButton(classes = "inline-block vertical") { +"게시" }
            textArea {
                wrap = TextAreaWrap.hard
                name = "contents"
                cols = "30"
//                +(article?.contents ?: "")
                placeholder = "내용"
                required = true
            }

        }
    }
}

fun HTML.article(user: Int?, article: Article) {
    head { styleCss() }
    body {
        getForm(classes = "inline-block vertical", action = "/articles") {
            getButton { +"이전으로" }
        }
        if (user == article.author.value) {
            postForm(classes = "inline-block vertical", action = "/articles/delete/${article.id}") {
                postButton { +"삭제하기" }
            }
            getForm(classes = "inline-block vertical", action = "/articles/edit/${article.id}") {
                getButton { +"수정하기" }
            }
        }
        h1 { +article.name }
        p { +article.contents }
    }
}
