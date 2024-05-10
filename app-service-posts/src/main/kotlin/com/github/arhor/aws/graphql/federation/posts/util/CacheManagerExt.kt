package com.github.arhor.aws.graphql.federation.posts.util

import org.springframework.cache.Cache
import org.springframework.cache.CacheManager

operator fun CacheManager.get(cache: Caches): Cache {
    return getCache(cache.name)
        ?: throw IllegalStateException("Cache $cache is not found!")
}
