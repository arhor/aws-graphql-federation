package com.github.arhor.aws.graphql.federation.spring.core.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration(proxyBeanMethods = false)
class ConfigureTestObjectMapper {

    @Bean
    fun objectMapper() = jacksonObjectMapper()
}
