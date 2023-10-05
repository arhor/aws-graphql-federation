package com.github.arhor.dgs.users.service.dto

data class CurrentUserRequest(
    val username: String,
    val password: String,
)
