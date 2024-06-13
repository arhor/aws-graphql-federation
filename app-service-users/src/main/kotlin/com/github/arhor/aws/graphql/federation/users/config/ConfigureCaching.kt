package com.github.arhor.aws.graphql.federation.users.config

import com.github.arhor.aws.graphql.federation.users.util.Caches
import com.github.arhor.aws.graphql.federation.users.util.get
import com.github.benmanes.caffeine.cache.Caffeine
import graphql.execution.preparsed.PreparsedDocumentProvider
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@EnableCaching
@Configuration(proxyBeanMethods = false)
class ConfigureCaching {

    @Bean
    fun caffeineCacheManagerCustomizer() = CacheManagerCustomizer<CaffeineCacheManager> {
        it.registerCustomCache(
            Caches.GRAPHQL_DOCUMENTS.name,
            Caffeine.newBuilder()
                .maximumSize(250)
                .expireAfterAccess(Duration.ofMinutes(10))
                .build()
        )
    }

    @Bean
    fun preparsedDocumentProvider(cacheManager: CacheManager): PreparsedDocumentProvider {
        val cache = cacheManager[Caches.GRAPHQL_DOCUMENTS]

        return PreparsedDocumentProvider { executionInput, parseAndValidateFunction ->
            cache.get(executionInput.query) {
                parseAndValidateFunction.apply(executionInput)
            }
        }
    }
}
