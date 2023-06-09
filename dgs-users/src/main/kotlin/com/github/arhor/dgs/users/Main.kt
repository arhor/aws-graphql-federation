package com.github.arhor.dgs.users

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.web.context.WebApplicationContext
import java.lang.invoke.MethodHandles

private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

@SpringBootApplication
class Main {

    @Bean
    @Profile("dev")
    fun <T> displayApplicationInfo(context: T)
        where T : WebApplicationContext,
              T : WebServerApplicationContext = ApplicationRunner {

        val port = context.webServer.port
        val path = context.servletContext?.contextPath ?: ""

        logger.info("Local access URL: http://localhost:{}{}", port, path)
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
