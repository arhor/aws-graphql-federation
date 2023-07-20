package com.github.arhor.dgs.users.config

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.KeyPair

@Configuration(proxyBeanMethods = false)
class ConfigureSecurity {

    @Bean
    fun jwtSigningKeyPair(): KeyPair = Keys.keyPairFor(SignatureAlgorithm.RS512)
}
