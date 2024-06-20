package com.github.arhor.aws.graphql.federation.comments.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheManagerUtils {

    private CacheManagerUtils() { /* Utils holder. Should not be instantiated */ }

    @NotNull
    public static Cache getCache(@NotNull final CacheManager cacheManager, @NotNull final Caches cache) {
        final var actualCache = cacheManager.getCache(cache.name());
        if (actualCache != null) {
            return actualCache;
        }
        throw new IllegalStateException("Cache " + cache + " is not found!");
    }
}
