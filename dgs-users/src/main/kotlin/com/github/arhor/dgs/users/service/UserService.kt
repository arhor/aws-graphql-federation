package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User

interface UserService {

    fun createNewUser(request: CreateUserRequest): User

    fun getUserByUsername(username: String): User

    fun getAllUsers(limit: Int, offset: Int): List<User>
}
