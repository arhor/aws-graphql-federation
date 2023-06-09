package com.github.arhor.dgs.users.config

import com.github.benmanes.caffeine.cache.Caffeine
import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.PreparsedDocumentProvider
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Function

@EnableCaching
@Configuration(proxyBeanMethods = false)
class ConfigureCaching {

    @Bean
    fun asyncCachePreParsedDocumentProvider(asyncExecutor: Executor): PreparsedDocumentProvider {
        return object : PreparsedDocumentProvider {
            private val logger = LoggerFactory.getLogger(javaClass)
            private val cache = Caffeine
                .newBuilder()
                .maximumSize(250)
                .expireAfterAccess(Duration.ofMinutes(10))
                .executor(asyncExecutor)
                .buildAsync<String, PreparsedDocumentEntry>()

            @Deprecated("Deprecated in Java")
            override fun getDocument(
                execInput: ExecutionInput,
                validator: Function<ExecutionInput, PreparsedDocumentEntry>,
            ): PreparsedDocumentEntry {

                logger.debug("Loading GQL document from the cache synchronously")

                return cache.synchronous().get(execInput.query) { currentQuery ->
                    logger.debug("Query-cache miss! query: {}", currentQuery)
                    validator.apply(execInput)
                }
            }

            override fun getDocumentAsync(
                execInput: ExecutionInput,
                validator: Function<ExecutionInput, PreparsedDocumentEntry>,
            ): CompletableFuture<PreparsedDocumentEntry> {

                logger.debug("Loading GQL document from the cache asynchronously")

                return cache.get(execInput.query) { currentQuery ->
                    logger.debug("Query-cache miss! query: {}", currentQuery)
                    validator.apply(execInput)
                }
            }
        }
    }
}
