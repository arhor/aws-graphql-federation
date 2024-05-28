package com.github.arhor.aws.graphql.federation.posts.config

import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.MessagingException
import org.springframework.retry.RetryOperations
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.support.RetryTemplate
import org.springframework.scheduling.annotation.EnableScheduling

@EnableRetry
@EnableScheduling
@EnableConfigurationProperties(AppProps::class)
@Configuration(proxyBeanMethods = false)
class ConfigureApplication {

    @Bean
    fun snsRetryOperations(appProps: AppProps): RetryOperations =
        appProps.retry!!.let {
            RetryTemplate.builder()
                .maxAttempts(it.maxAttempts)
                .exponentialBackoff(it.minInterval, it.multiplier.toDouble(), it.maxInterval)
                .retryOn(MessagingException::class.java)
                .traversingCauses()
                .build()
        }
}
