package com.github.arhor.aws.graphql.federation.starter.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class PredefinedAuthority : GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN,
    ;

    override fun getAuthority(): String = name

    fun asSimpleGrantedAuthority() = SimpleGrantedAuthority(name)
}
