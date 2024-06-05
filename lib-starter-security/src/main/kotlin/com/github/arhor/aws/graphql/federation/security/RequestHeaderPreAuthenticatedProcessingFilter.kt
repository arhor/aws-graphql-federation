package com.github.arhor.aws.graphql.federation.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter

class RequestHeaderPreAuthenticatedProcessingFilter(
    private val objectMapper: ObjectMapper,
) : AbstractPreAuthenticatedProcessingFilter() {

    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any? {
        val header = request.getHeader(HEADER_CURRENT_USER)
            ?: return null

        return objectMapper.readValue(header, CurrentUser::class.java)
            .also { request.setAttribute(Attributes.CURRENT_USER_AUTHORITIES, it.authorities) }
            .id
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any {
        return "N/A"
    }

    companion object {
        private const val HEADER_CURRENT_USER = "x-current-user"
    }
}
