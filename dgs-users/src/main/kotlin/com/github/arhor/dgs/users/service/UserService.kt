package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.generated.graphql.types.UsersLookupInput

interface UserService {
    fun getUserById(id: Long): User
    fun getAllUsers(input: UsersLookupInput): List<User>
    fun createUser(input: CreateUserInput): User
    fun updateUser(input: UpdateUserInput): User
    fun deleteUser(input: DeleteUserInput): Boolean
}
