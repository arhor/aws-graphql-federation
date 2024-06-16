package com.github.arhor.aws.graphql.federation.starter.graphql

import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.internal.method.ArgumentResolver
import com.netflix.graphql.dgs.mvc.internal.method.HandlerMethodArgumentResolverAdapter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver

@ComponentScan
@AutoConfiguration(before = [DgsAutoConfiguration::class])
class SubgraphComponentsAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(AuthenticationPrincipalArgumentResolver::class)
    class AuthenticationPrincipalDgsArgumentResolverConfig {
        @Bean
        fun authenticationPrincipalDgsArgumentResolver(): ArgumentResolver {
            return HandlerMethodArgumentResolverAdapter(
                delegate = AuthenticationPrincipalArgumentResolver()
            )
        }
    }
}
