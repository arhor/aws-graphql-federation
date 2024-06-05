package com.github.arhor.aws.graphql.federation.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider

@EnableWebSecurity
@EnableMethodSecurity
@Configuration(proxyBeanMethods = false)
class SecurityBeansConfig {

    @Bean
    fun defaultSecurityFilterChain(
        http: HttpSecurity,
        authConfig: AuthenticationConfiguration,
        objectMapper: ObjectMapper,
    ): SecurityFilterChain {
        http {
            csrf { disable() }
            logout { disable() }
            httpBasic { disable() }
            formLogin { disable() }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
            addFilterBefore<AnonymousAuthenticationFilter>(
                RequestHeaderPreAuthenticatedProcessingFilter(objectMapper).apply {
                    setAuthenticationManager(authConfig.authenticationManager)
                    setAuthenticationDetailsSource(RequestAttributesAuthenticationDetailsSource)
                }
            )
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder() =
        BCryptPasswordEncoder()

    @Bean
    fun preAuthenticatedAuthenticationProvider() =
        PreAuthenticatedAuthenticationProvider().apply {
            setPreAuthenticatedUserDetailsService(
                PreAuthenticatedGrantedAuthoritiesCurrentUserDetailsService
            )
        }
}

