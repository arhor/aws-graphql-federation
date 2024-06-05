package com.github.arhor.aws.graphql.federation.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails as PreAuthDetails

object RequestAttributesAuthenticationDetailsSource : AuthenticationDetailsSource<HttpServletRequest, PreAuthDetails> {

    override fun buildDetails(context: HttpServletRequest): PreAuthDetails {
        val authorities =
            (context.getAttribute(Attributes.CURRENT_USER_AUTHORITIES) as? List<*>)
                ?.filterNotNull()
                ?.map { SimpleGrantedAuthority(it.toString()) }
                ?: emptyList()

        return PreAuthDetails(context, authorities)
    }
}
