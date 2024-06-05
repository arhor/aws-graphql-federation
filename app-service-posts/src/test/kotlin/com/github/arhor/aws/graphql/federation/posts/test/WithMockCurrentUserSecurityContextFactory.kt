package com.github.arhor.aws.graphql.federation.posts.test

import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.util.UUID

class WithMockCurrentUserSecurityContextFactory : WithSecurityContextFactory<WithMockCurrentUser> {

    private var securityContextHolderStrategy: SecurityContextHolderStrategy =
        SecurityContextHolder.getContextHolderStrategy()

    @Autowired(required = false)
    fun setSecurityContextHolderStrategy(securityContextHolderStrategy: SecurityContextHolderStrategy) {
        this.securityContextHolderStrategy = securityContextHolderStrategy
    }

    override fun createSecurityContext(user: WithMockCurrentUser): SecurityContext {
        val id = UUID.fromString(user.id.ifBlank { user.value })

        val grantedAuthorities = user.authorities.map(::SimpleGrantedAuthority).toMutableSet()

        if (grantedAuthorities.isEmpty()) {
            for (role: String in user.roles) {
                check(!role.startsWith("ROLE_")) { "roles cannot start with ROLE_ Got $role" }

                grantedAuthorities.add(SimpleGrantedAuthority("ROLE_$role"))
            }
        } else if ("USER" != user.roles.single()) {
            throw IllegalStateException(
                "You cannot define roles attribute %s with authorities attribute %s".format(
                    user.roles.contentToString(),
                    user.authorities.contentToString(),
                )
            )
        }

        return securityContextHolderStrategy.createEmptyContext().apply {
            authentication =
                CurrentUserDetails(id, grantedAuthorities).let {
                    UsernamePasswordAuthenticationToken.authenticated(
                        it,
                        it.password,
                        it.authorities,
                    )
                }
        }
    }
}
