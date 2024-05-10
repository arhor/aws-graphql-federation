package com.github.arhor.aws.graphql.federation.comments.util;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheManagerUtils {

    private CacheManagerUtils() {
        throw new UnsupportedOperationException("Cannot be instantiated!");
    }

    public static Cache getCache(final CacheManager cacheManager, final Caches cache) {
        final var actualCache = cacheManager.getCache(cache.name());
        if (actualCache != null) {
            return actualCache;
        }
        throw new IllegalStateException("Cache " + cache + " is not found!");
    }
}
