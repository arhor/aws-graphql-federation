package com.github.arhor.aws.graphql.federation.posts.util

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.InstanceOfAssertFactories.throwable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager

class CacheManagerExtTest {

    private val cacheManager = mockk<CacheManager>()
    private val cache = mockk<Cache>()

    @Nested
    @DisplayName("Method get")
    inner class GetTest {
        @EnumSource
        @ParameterizedTest
        fun `should return expected cache instance when CacheManager#getCache returned not null`(
            // Given
            cacheName: Caches,
        ) {
            every { cacheManager.getCache(any()) } returns cache

            // When
            val result = cacheManager[cacheName]

            // Then
            verify(exactly = 1) { cacheManager.getCache(cacheName.name) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(cache)
        }

        @EnumSource
        @ParameterizedTest
        fun `should throw IllegalStateException when CacheManager#getCache returned null`(
            // Given
            cacheName: Caches,
        ) {
            every { cacheManager.getCache(any()) } returns null

            // When
            val result = catchException { cacheManager[cacheName] }

            // Then
            verify(exactly = 1) { cacheManager.getCache(cacheName.name) }

            assertThat(result)
                .isNotNull()
                .asInstanceOf(throwable(IllegalStateException::class.java))
                .hasMessage("Cache $cacheName is not found!")
        }
    }
}
