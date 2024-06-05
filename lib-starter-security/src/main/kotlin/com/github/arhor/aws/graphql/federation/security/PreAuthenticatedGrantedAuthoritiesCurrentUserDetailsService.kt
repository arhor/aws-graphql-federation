package com.github.arhor.aws.graphql.federation.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService
import java.util.UUID

object PreAuthenticatedGrantedAuthoritiesCurrentUserDetailsService :
    PreAuthenticatedGrantedAuthoritiesUserDetailsService() {

    override fun createUserDetails(token: Authentication, authorities: Collection<GrantedAuthority>): UserDetails {
        return CurrentUserDetails(
            id = token.principal as UUID,
            authorities = authorities,
        )
    }
}
