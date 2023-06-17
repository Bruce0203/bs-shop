package org.example.application.template

import kotlinx.html.*

class CUSTOM(tag: String, consumer: TagConsumer<*>) :
        HTMLTag(tag, consumer, emptyMap(),
                inlineTag = true, 
                emptyTag = false), HtmlInlineTag, HtmlBlockTag {
}

fun DIV.custom(tag: String, block: CUSTOM.() -> Unit = {}) {
        CUSTOM(tag, consumer).visit(block)
}

fun FORM.custom(tag: String, block: CUSTOM.() -> Unit = {}) {
        CUSTOM(tag, consumer).visit(block)
}

fun DIALOG.custom(tag: String, block: CUSTOM.() -> Unit = {}) {
        CUSTOM(tag, consumer).visit(block)
}

fun <T> TagConsumer<T>.custom(tag: String, block: CUSTOM.() -> Unit = {}): T {
        return CUSTOM(tag, this).visitAndFinalize(this, block)
}

