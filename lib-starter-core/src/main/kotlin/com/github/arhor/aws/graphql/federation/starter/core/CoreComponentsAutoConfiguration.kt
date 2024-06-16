package com.github.arhor.aws.graphql.federation.starter.core

import jakarta.validation.ClockProvider
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.core.task.TaskDecorator
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Optional

@ComponentScan
@AutoConfiguration
class CoreComponentsAutoConfiguration {

    @Bean
    fun clockProvider(): ClockProvider = ClockProvider {
        Clock.systemUTC()
    }

    @Bean
    fun currentDateTimeProvider(clockProvider: ClockProvider): DateTimeProvider = DateTimeProvider {
        val currClock = clockProvider.clock
        val timestamp = LocalDateTime.now(currClock)

        Optional.of(timestamp.truncatedTo(ChronoUnit.MILLIS))
    }

    @Bean
    @Profile("dev", "!test")
    fun <T> displayApplicationInfo(context: T): ApplicationRunner
        where T : WebApplicationContext,
              T : WebServerApplicationContext = ApplicationRunner {

        val port = context.webServer.port
        val path = context.servletContext?.contextPath ?: ""

        logger.info("Local access URL: http://localhost:{}{}", port, path)
    }


    @Bean
    fun parentContextTaskDecorator(): TaskDecorator = TaskDecorator {
        val attributes = RequestContextHolder.getRequestAttributes()
        val contextMap = MDC.getCopyOfContextMap()

        return@TaskDecorator if (attributes == null && contextMap == null) it else Runnable {
            try {
                RequestContextHolder.setRequestAttributes(attributes)
                MDC.setContextMap(contextMap ?: emptyMap())
                it.run()
            } finally {
                MDC.clear()
                RequestContextHolder.resetRequestAttributes()
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
