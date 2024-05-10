package com.github.arhor.aws.graphql.federation.comments.config;

import com.github.arhor.aws.graphql.federation.comments.util.Caches;
import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.github.arhor.aws.graphql.federation.comments.util.CacheManagerUtils.getCache;

@EnableCaching
@Configuration(proxyBeanMethods = false)
public class ConfigureCaching {

    @Bean
    public CacheManagerCustomizer<CaffeineCacheManager> caffeineCacheManagerCustomizer() {
        return (it) -> {
            it.registerCustomCache(
                Caches.GRAPHQL_DOCUMENTS.name(),
                Caffeine.newBuilder()
                    .maximumSize(250)
                    .expireAfterAccess(Duration.ofMinutes(10))
                    .build()
            );
            it.registerCustomCache(
                Caches.IDEMPOTENT_ID_SET.name(),
                Caffeine.newBuilder()
                    .maximumSize(10_000)
                    .expireAfterAccess(Duration.ofMinutes(30))
                    .build()
            );
        };
    }

    @Bean
    public PreparsedDocumentProvider preparsedDocumentProvider(final CacheManager cacheManager) {
        final var cache = getCache(cacheManager, Caches.GRAPHQL_DOCUMENTS);

        return (executionInput, parseAndValidateFunction) -> cache.get(
            executionInput.getQuery(),
            () -> parseAndValidateFunction.apply(executionInput)
        );
    }
}
