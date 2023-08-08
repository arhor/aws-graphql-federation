package com.github.arhor.dgs.lib

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.web.context.WebApplicationContext

@ComponentScan
@AutoConfiguration(
    before = [
        TaskExecutionAutoConfiguration::class,
    ]
)
class EntryPoint {

    private val logger = LoggerFactory.getLogger(javaClass)

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