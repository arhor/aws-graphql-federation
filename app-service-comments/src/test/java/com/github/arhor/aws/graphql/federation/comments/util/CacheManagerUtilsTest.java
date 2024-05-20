package com.github.arhor.aws.graphql.federation.comments.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.InstanceOfAssertFactories.throwable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class CacheManagerUtilsTest {

    private final CacheManager cacheManager = mock();
    private final Cache cache = mock();

    @Nested
    @DisplayName("CacheManager :: get")
    class GetTest {
        @EnumSource
        @ParameterizedTest
        void should_return_expected_cache_instance_when_CacheManager_getCache_returned_not_null(
            // Given
            final Caches cacheName
        ) {
            given(cacheManager.getCache(any()))
                .willReturn(cache);

            // When
            final var result = CacheManagerUtils.getCache(cacheManager, cacheName);

            // Then
            then(cacheManager)
                .should()
                .getCache(cacheName.name());

            assertThat(result)
                .isNotNull()
                .isEqualTo(cache);
        }

        @EnumSource
        @ParameterizedTest
        void should_throw_IllegalStateException_when_CacheManager_getCache_returned_null(
            // Given
            final Caches cacheName
        ) {
            given(cacheManager.getCache(any()))
                .willReturn(null);

            // When
            final var result = catchException(() -> CacheManagerUtils.getCache(cacheManager, cacheName));

            // Then
            then(cacheManager)
                .should()
                .getCache(cacheName.name());

            assertThat(result)
                .isNotNull()
                .asInstanceOf(throwable(IllegalStateException.class))
                .hasMessage("Cache " + cacheName + " is not found!");
        }
    }
}
