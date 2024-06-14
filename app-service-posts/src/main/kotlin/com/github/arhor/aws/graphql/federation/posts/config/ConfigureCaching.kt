package com.github.arhor.aws.graphql.federation.posts.config

import com.github.arhor.aws.graphql.federation.posts.util.Caches
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer
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
            Caches.IDEMPOTENT_ID_SET.name,
            Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(Duration.ofMinutes(30))
                .build()
        )
    }
}
