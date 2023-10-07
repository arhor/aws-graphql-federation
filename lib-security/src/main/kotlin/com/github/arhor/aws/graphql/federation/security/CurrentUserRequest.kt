package com.github.arhor.aws.graphql.federation.security

data class CurrentUserRequest(
    val username: String,
    val password: String,
)
