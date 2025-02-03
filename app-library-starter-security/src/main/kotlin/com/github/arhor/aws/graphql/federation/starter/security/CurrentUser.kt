package com.github.arhor.aws.graphql.federation.starter.security

import java.util.UUID

data class CurrentUser(
    val id: UUID,
    val authorities: List<String>,
)
