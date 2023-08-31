package com.github.arhor.dgs.users.api.graphql

class CurrentExecutionContext(
    val currentUser: CurrentUser?
)

class CurrentUser(
    val id: Long,
    val authorities: List<String>,
)
