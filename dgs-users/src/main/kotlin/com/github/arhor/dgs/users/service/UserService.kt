package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.User

interface UserService {
    fun createUser(input: CreateUserInput): User
    fun updateUser(input: UpdateUserInput): User
    fun deleteUser(id: Long): Boolean
    fun getUserById(id: Long): User
    fun getUserByUsername(username: String): User
    fun getAllUsers(limit: Int, offset: Int): List<User>
}
