package com.github.arhor.dgs.users.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.util.matcher.RequestMatcher

class PreAuthenticatedUserAuthenticationProcessingFilter(
    requestMatcher: RequestMatcher,
    authManager: AuthenticationManager,
) : AbstractAuthenticationProcessingFilter(requestMatcher, authManager) {

    companion object {
        const val PRE_AUTHENTICATED_USER_HEADER = "x-current-user"
    }

    override fun attemptAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse,
    ): Authentication? {
        return req.getHeader(PRE_AUTHENTICATED_USER_HEADER)
            ?.let { PreAuthenticatedAuthenticationToken(it, null) }
            ?.let { authenticationManager.authenticate(it) }
    }

    override fun successfulAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse,
        next: FilterChain,
        auth: Authentication,
    ) {
        SecurityContextHolder.getContext().authentication = auth
        next.doFilter(req, res)
    }
}
