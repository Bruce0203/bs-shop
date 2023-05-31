package io.github.bruce0203.watchmore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class WatchmoreApplication

fun main(args: Array<String>) {
	runApplication<WatchmoreApplication>(*args)
}
