package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User

interface UserService {
    fun createUser(request: CreateUserRequest): User
    fun updateUser(request: UpdateUserRequest): User
    fun deleteUser(userId: Long): Boolean
    fun getUserByUsername(username: String): User
    fun getAllUsers(limit: Int, offset: Int): List<User>
}
