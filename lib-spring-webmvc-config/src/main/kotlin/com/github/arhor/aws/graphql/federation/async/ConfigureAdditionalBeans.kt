package com.github.arhor.aws.graphql.federation.async

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.context.WebApplicationContext
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Supplier

@Configuration(proxyBeanMethods = false)
class ConfigureAdditionalBeans {

    @Bean
    fun currentDateTimeSupplier() = Supplier {
        logger.info(">>> TEST")

        val systemUTC = Clock.systemUTC()
        val timestamp = LocalDateTime.now(systemUTC)

        logger.info("<<< TEST")
        timestamp.truncatedTo(ChronoUnit.MILLIS)
    }

    @Bean
    @Profile("dev", "!test")
    fun <T> displayApplicationInfo(context: T)
        where T : WebApplicationContext,
              T : WebServerApplicationContext = ApplicationRunner {

        val port = context.webServer.port
        val path = context.servletContext?.contextPath ?: ""

        logger.info("Local access URL: http://localhost:{}{}", port, path)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
