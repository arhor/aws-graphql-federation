package com.github.arhor.aws.graphql.federation.users.config

import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@EnableRetry
@EnableScheduling
@EnableConfigurationProperties(AppProps::class)
@Configuration(proxyBeanMethods = false)
class ConfigureApplication
