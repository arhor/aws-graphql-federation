package com.github.arhor.aws.graphql.federation.comments.util;

import jakarta.annotation.Nonnull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheManagerUtils {

    private CacheManagerUtils() { /* Utils holder. Should not be instantiated */ }

    @Nonnull
    public static Cache getCache(@Nonnull final CacheManager cacheManager, @Nonnull final Caches cache) {
        final var actualCache = cacheManager.getCache(cache.name());
        if (actualCache != null) {
            return actualCache;
        }
        throw new IllegalStateException("Cache " + cache + " is not found!");
    }
}
