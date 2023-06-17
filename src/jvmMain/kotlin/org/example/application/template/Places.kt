package org.example.application.template

import kotlinx.html.*
import org.example.application.model.*
import org.example.application.route.RentTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

const val serviceName = "백신고 교내 시설 대여"

fun HTML.places(user: User?) {
    head {
        styleCss()
        title { +serviceName }
    }
    body {
        h1 { +serviceName }
        p { +"학생들이 공부, 스포츠 등 다양한 활동을 위해 교내 시설 이용이 필요한 경우 이들에게 간편하게 확인할 수 있는 사용하고 있는 시설과 사용할 수 있는 시설을 보여주고, 쉽게 대여할 수 있도록 하여 편의를 제공하고자 합니다." }
        div {
            println(user)
            if(user?.isAdmin == true) {
                getForm(classes = "inline-block vertical", action = "/places/new") {
                    getButton { +"시설 등록하기" }
                }
            }
        }
        br
        transaction { Place.all().map { it } }.forEach { place ->
            a(classes = "no-text-color") {
                id = "${place.id.value}"
                onClick = transaction { Rent.find(Rents.place eq place.id.value)
                    .firstOrNull { it.expiredOn.isAfter(LocalDateTime.now()) }
                    ?.let { "cannotRent(${place.id.value})" }
                    ?: "rent(${place.id.value});" }
                article("no-text-color") {
                    h2(classes = "no-top vertical inline-block") { +place.name }
                    cite(classes = "no-top right vertical inline-block") {
                        +(transaction { Rent.find(Rents.place eq place.id.value)
                            .firstOrNull { it.expiredOn.isAfter(LocalDateTime.now()) }
                            ?.let { "${User[it.author].displayName}님이 대여중" }
                            ?: "대여 가능"
                        })
                    }
                    p("no-text-color no-step") { +place.contents }
                    br;br;
                }
                dialog {
                    attributes["style"] = "display: none;"
                    id = "rent-dialog-${place.id.value}"
                    header { +place.name }
                    p("center") { +"대여할 시간을 선택해주세요" }
                    if(user?.isAdmin == true) {
                        getForm(classes = "", action = "/places/edit/${place.id}") {
                            getButton { +"시설 수정하기" }
                        }
                    }
                    RentTime.values().forEach {
                        custom("menu") {
                            getForm(action = "/rent/${place.id.value}") {
//                        attributes["method"] = "dialog"
                                hiddenInput(name = "time") { value = it.toString() }
                                getButton { +it.displayName }
                            }
                        }
                    }
                }
                dialog {
                    attributes["style"] = "display: none;"
                    id = "cannot-rent-dialog-${place.id.value}"
                    header { +place.name }
                    p ("center") { +"대여할 수 없습니다" }
                    getForm(action = "/") {
                        div("center") {
                            custom("menu") {
                                getButton { +"돌아가기" }
                            }
                        }
                    }
                }
            }
            script {
                +("""
                    function rent(id) {
                        let dialog = document.getElementById('rent-dialog-' + id)
                        dialog.style.display = null
                        dialog.showModal()
                    }
                    function cannotRent(id) {
                        let dialog = document.getElementById('cannot-rent-dialog-' + id)
                        dialog.style.display = null
                        dialog.showModal()
                    }

                """.trimIndent())
            }
        }
        footer {}
    }
}

fun HTML.placeEditor(username: String?, place: Place? = null) {
    head { styleCss() }
    body {
        h1 { +(if (place === null) "시설 등록하기" else "시설 수정하기") }
        postForm(action = if (place === null) "/places" else "/places/${place.id}") {
            name = "editor"
            textInput(classes = "inline-block vertical", name = "name") {
                value = place?.name ?: username?.let { "${it}님의 글" } ?: ""
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

fun HTML.place(user: User?, place: Place) {
    head { styleCss() }
    body {
        getForm(classes = "inline-block vertical", action = "/places") {
            getButton { +"이전으로" }
        }
        getForm(classes = "inline-block vertical", action = "/rent/${place.id}") {
            getButton { +"대여하기" }
        }
        if(user?.isAdmin == true) {
            postForm(classes = "inline-block vertical", action = "/places/delete/${place.id}") {
                postButton { +"삭제하기" }
            }
            getForm(classes = "inline-block vertical", action = "/places/edit/${place.id}") {
                getButton { +"수정하기" }
            }
        }
        h1 { +place.name }
        p { +place.contents }
    }
}
