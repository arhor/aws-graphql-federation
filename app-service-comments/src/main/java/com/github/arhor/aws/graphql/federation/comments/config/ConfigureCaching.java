package com.github.arhor.aws.graphql.federation.comments.config;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.ExecutionInput;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@EnableCaching
@Configuration(proxyBeanMethods = false)
public class ConfigureCaching {

    @Bean
    public PreparsedDocumentProvider asyncCachePreParsedDocumentProvider(final Executor executor) {
        return new PreparsedDocumentProvider() {
            private final AsyncCache<String, PreparsedDocumentEntry> cache = Caffeine
                .newBuilder()
                .maximumSize(250)
                .expireAfterAccess(Duration.ofMinutes(10))
                .executor(executor)
                .buildAsync();

            @Override
            public PreparsedDocumentEntry getDocument(
                final ExecutionInput executionInput,
                final Function<ExecutionInput, PreparsedDocumentEntry> parseAndValidateFunction
            ) {
                return cache
                    .synchronous()
                    .get(executionInput.getQuery(), (it) -> parseAndValidateFunction.apply(executionInput));
            }

            @Override
            public CompletableFuture<PreparsedDocumentEntry> getDocumentAsync(
                final ExecutionInput executionInput,
                final Function<ExecutionInput, PreparsedDocumentEntry> parseAndValidateFunction
            ) {
                return cache
                    .get(executionInput.getQuery(), (it) -> parseAndValidateFunction.apply(executionInput));
            }
        };
    }
}
