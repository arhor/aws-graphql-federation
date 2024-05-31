package com.github.arhor.aws.graphql.federation.security

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component

/**
 * @see [PreAuthenticatedAuthenticationProvider]
 */
@Component
class PreAuthenticatedUserAuthenticationProvider(
    private val objectMapper: ObjectMapper,
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication? {
        if (!supports(authentication.javaClass)) {
            return null
        }
        try {
            val data = authentication.principal as String
            val user = objectMapper.readValue(data, CurrentUserTypeRef)

            val authorities = user.authorities.map(::SimpleGrantedAuthority)
            val userUuidStr = user.id.toString()

            val principal =
                User.builder()
                    .username(userUuidStr)
                    .password("N/A")
                    .authorities(authorities)
                    .build()

            return PreAuthenticatedAuthenticationToken(principal, principal.password, principal.authorities)
        } catch (e: Exception) {
            throw AccessDeniedException("Invalid authentication token", e)
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return PreAuthenticatedAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    private object CurrentUserTypeRef : TypeReference<CurrentUser>()
}
