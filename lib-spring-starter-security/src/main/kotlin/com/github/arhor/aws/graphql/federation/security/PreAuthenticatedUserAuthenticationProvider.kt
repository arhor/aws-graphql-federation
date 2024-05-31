package com.github.arhor.aws.graphql.federation.security

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
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
        try {
            val data = authentication.principal as String
            val user = objectMapper.readValue(data, CurrentUserTypeRef)

            return PreAuthenticatedAuthenticationToken(
                user.id,
                null,
                user.authorities.map(::SimpleGrantedAuthority)
            )
        } catch (e: Exception) {
            throw AccessDeniedException("Invalid authentication token", e)
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return PreAuthenticatedAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    private object CurrentUserTypeRef : TypeReference<CurrentUser>()
}
