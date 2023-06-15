package com.github.arhor.dgs.users.graphql.fetcher

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.UserService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class UserFetcher(private val userService: UserService) {

    @DgsMutation
    fun createUser(@InputArgument request: CreateUserRequest): User {
        return userService.createUser(request)
    }

    @DgsMutation
    fun deleteUser(@InputArgument userId: Long): Boolean {
        return userService.deleteUser(userId)
    }

    @DgsQuery
    fun users(@InputArgument offset: Int, @InputArgument limit: Int): List<User> {
        return userService.getAllUsers(limit = limit, offset = offset)
    }

    @DgsQuery
    fun user(@InputArgument username: String): User {
        return userService.getUserByUsername(username)
    }
}
