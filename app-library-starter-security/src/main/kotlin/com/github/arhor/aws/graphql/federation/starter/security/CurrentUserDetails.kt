package com.github.arhor.aws.graphql.federation.starter.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.UUID

class CurrentUserDetails(
    val id: UUID,
    authorities: Collection<GrantedAuthority>,
) : User("N/A", "N/A", authorities)
