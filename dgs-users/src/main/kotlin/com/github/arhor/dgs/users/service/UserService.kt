package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.common.Limit
import com.github.arhor.dgs.users.common.Offset

interface UserService {

    fun createNewUser(request: CreateUserRequest): User

    fun getUserByUsername(username: String): User

    fun getAllUsers(offset: Offset, limit: Limit): List<User>
}
