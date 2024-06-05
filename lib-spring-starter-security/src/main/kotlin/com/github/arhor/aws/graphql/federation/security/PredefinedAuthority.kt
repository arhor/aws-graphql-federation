package com.github.arhor.aws.graphql.federation.security

import org.springframework.security.core.GrantedAuthority

enum class PredefinedAuthority : GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN,
    ;

    override fun getAuthority(): String = name
}
