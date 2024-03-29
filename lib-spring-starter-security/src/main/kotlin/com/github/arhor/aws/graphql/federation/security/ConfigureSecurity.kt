package com.github.arhor.aws.graphql.federation.security

import com.github.arhor.aws.graphql.federation.security.PreAuthenticatedUserAuthenticationProcessingFilter.Companion.PRE_AUTHENTICATED_USER_HEADER
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher

@ComponentScan
@AutoConfiguration
@EnableWebSecurity
@EnableMethodSecurity
class ConfigureSecurity {

    @Bean
    fun defaultSecurityFilterChain(
        http: HttpSecurity,
        authConfig: AuthenticationConfiguration,
    ): SecurityFilterChain {
        http {
            csrf { disable() }
            logout { disable() }
            httpBasic { disable() }
            formLogin { disable() }

            sessionManagement {
                sessionCreationPolicy = STATELESS
            }
            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
            addFilterBefore<AnonymousAuthenticationFilter>(
                PreAuthenticatedUserAuthenticationProcessingFilter(
                    RequestHeaderRequestMatcher(PRE_AUTHENTICATED_USER_HEADER),
                    authConfig.authenticationManager,
                )
            )
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}
