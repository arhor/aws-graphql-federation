package com.github.arhor.dgs.lib.config

import com.github.benmanes.caffeine.cache.Caffeine
import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.PreparsedDocumentProvider
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.concurrent.Executor
import java.util.function.Function

private typealias ParsingValidator = Function<ExecutionInput, PreparsedDocumentEntry>

@EnableCaching
@Configuration(proxyBeanMethods = false)
class ConfigureCaching {

    @Bean
    @Suppress("OVERRIDE_DEPRECATION")
    fun asyncCachePreParsedDocumentProvider(asyncExecutor: Executor): PreparsedDocumentProvider =
        object : PreparsedDocumentProvider {
            private val cache = buildAsyncCache<String, PreparsedDocumentEntry> {
                maximumSize(250)
                expireAfterAccess(Duration.ofMinutes(10))
                executor(asyncExecutor)
            }

            override fun getDocument(execInput: ExecutionInput, validator: ParsingValidator) =
                cache.synchronous().get(execInput.query) { _ -> validator.apply(execInput) }

            override fun getDocumentAsync(execInput: ExecutionInput, validator: ParsingValidator) =
                cache.get(execInput.query) { _ -> validator.apply(execInput) }
        }

    private inline fun <K, V> buildAsyncCache(init: Caffeine<Any, Any>.() -> Unit) =
        Caffeine.newBuilder().apply { init() }.buildAsync<K, V>()
}
