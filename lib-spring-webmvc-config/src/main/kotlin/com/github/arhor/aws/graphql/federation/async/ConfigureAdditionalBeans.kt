package com.github.arhor.aws.graphql.federation.async

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
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

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun currentDateTimeSupplier() = Supplier {
        val systemUTC = Clock.systemUTC()
        val timestamp = LocalDateTime.now(systemUTC)

        timestamp.truncatedTo(ChronoUnit.MILLIS)
    }

    @Bean
    @Profile("dev")
    fun <T> displayApplicationInfo(context: ObjectProvider<T>)
        where T : WebApplicationContext,
              T : WebServerApplicationContext = ApplicationRunner {

        context.ifAvailable {
            val port = it.webServer.port
            val path = it.servletContext?.contextPath ?: ""

            logger.info("Local access URL: http://localhost:{}{}", port, path)
        }
    }
}
