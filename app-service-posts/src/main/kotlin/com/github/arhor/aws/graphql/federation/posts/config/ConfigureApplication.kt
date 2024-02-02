package com.github.arhor.aws.graphql.federation.posts.config

import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@EnableConfigurationProperties(AppProps::class)
@Configuration(proxyBeanMethods = false)
class ConfigureApplication
