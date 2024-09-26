package com.github.arhor.aws.graphql.federation.starter.core

import io.micrometer.context.ContextRegistry
import io.micrometer.context.ContextSnapshotFactory
import io.micrometer.context.integration.Slf4jThreadLocalAccessor
import jakarta.validation.ClockProvider
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.core.task.TaskDecorator
import org.springframework.core.task.support.CompositeTaskDecorator
import org.springframework.core.task.support.ContextPropagatingTaskDecorator
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.web.context.WebApplicationContext
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Optional
import java.util.function.Supplier

@ComponentScan
@AutoConfiguration(before = [TaskExecutionAutoConfiguration::class])
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
    fun compositeTaskDecorator(decorators: List<Supplier<TaskDecorator>>): TaskDecorator =
        CompositeTaskDecorator(
            decorators.map {
                it.get()
            }
        )

    @Bean
    fun contextPropagatingTaskDecorator(): Supplier<TaskDecorator> =
        Supplier {
            ContextPropagatingTaskDecorator(
                ContextSnapshotFactory.builder()
                    .contextRegistry(
                        ContextRegistry()
                            .loadContextAccessors()
                            .loadThreadLocalAccessors()
                            .registerThreadLocalAccessor(Slf4jThreadLocalAccessor())
                    )
                    .build()
            )
        }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
