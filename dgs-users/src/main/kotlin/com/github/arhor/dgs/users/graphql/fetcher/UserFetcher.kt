package com.github.arhor.dgs.users.graphql.fetcher

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.UserService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class UserFetcher(
    private val userService: UserService,
) {

    @DgsMutation
    fun createUser(@InputArgument request: CreateUserRequest): User =
        userService.createUser(request)

    @DgsMutation
    fun updateUser(@InputArgument request: UpdateUserRequest): User =
        userService.updateUser(request)

    @DgsMutation
    fun deleteUser(@InputArgument userId: Long): Boolean =
        userService.deleteUser(userId)

    @DgsQuery
    fun user(@InputArgument username: String): User =
        userService.getUserByUsername(username)

    @DgsQuery
    fun users(@InputArgument limit: Int, @InputArgument offset: Int): List<User> =
        userService.getAllUsers(limit = limit, offset = offset)
}
