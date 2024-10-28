package com.github.arhor.aws.graphql.federation.starter.testing

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheType
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ExtendWith(SpringExtension::class)
@ContextConfiguration
@AutoConfigureCache(cacheProvider = CacheType.REDIS)
@ImportAutoConfiguration(RedisAutoConfiguration::class)
@OverrideAutoConfiguration(enabled = false)
annotation class RedisCacheTest(
    @get:AliasFor(annotation = ContextConfiguration::class, attribute = "classes")
    val classes: Array<KClass<*>> = [],
)
