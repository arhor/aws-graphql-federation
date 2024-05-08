package com.github.arhor.aws.graphql.federation.security

import java.util.UUID

data class CurrentUser(
    val id: UUID,
    val authorities: List<String>,
)
