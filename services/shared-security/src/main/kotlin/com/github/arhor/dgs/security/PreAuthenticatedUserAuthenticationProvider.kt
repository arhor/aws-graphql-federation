package com.github.arhor.dgs.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component

@Component
class PreAuthenticatedUserAuthenticationProvider(
    private val objectMapper: ObjectMapper,
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        return try {
            objectMapper.readValue<User>(authentication.principal as String).let {
                PreAuthenticatedAuthenticationToken(
                    it.id,
                    null,
                    it.authorities.map(::SimpleGrantedAuthority)
                )
            }
        } catch (e: Exception) {
            throw AccessDeniedException("Invalid authentication token", e)
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return PreAuthenticatedAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    data class User(val id: Long, val authorities: List<String>)
}
