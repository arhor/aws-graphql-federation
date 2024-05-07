package com.github.arhor.aws.graphql.federation.spring.core

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.core.task.TaskDecorator
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Supplier

@ComponentScan
@AutoConfiguration
class ConfigureCoreApplicationComponents {

    @Bean
    fun currentDateTimeSupplier(): Supplier<LocalDateTime> = Supplier {
        val systemUTC = Clock.systemUTC()
        val timestamp = LocalDateTime.now(systemUTC)

        timestamp.truncatedTo(ChronoUnit.MILLIS)
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

        if (attributes == null && contextMap == null) it else Runnable {
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
