package com.github.arhor.aws.graphql.federation.posts.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

@EnableCaching
@Configuration(proxyBeanMethods = false)
class ConfigureCaching
