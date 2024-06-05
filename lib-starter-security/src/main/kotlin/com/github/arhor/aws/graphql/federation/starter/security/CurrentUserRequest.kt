package com.github.arhor.aws.graphql.federation.starter.security

data class CurrentUserRequest(
    val username: String,
    val password: String,
)
