package com.github.arhor.aws.graphql.federation.security

data class CurrentUser(
    val id: Long,
    val authorities: List<String>,
)
