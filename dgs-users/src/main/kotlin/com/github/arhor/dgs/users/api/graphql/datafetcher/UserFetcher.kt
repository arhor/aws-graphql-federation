package com.github.arhor.dgs.users.api.graphql.datafetcher

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.dgs.users.service.UserService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class UserFetcher(private val userService: UserService) {

    @DgsQuery
    fun user(@InputArgument id: Long): User =
        userService.getUserById(id)

    @DgsQuery
    fun users(@InputArgument input: UsersLookupInput): List<User> =
        userService.getAllUsers(input)

    @DgsMutation
    fun createUser(@InputArgument input: CreateUserInput): User =
        userService.createUser(input)

    @DgsMutation
    fun updateUser(@InputArgument input: UpdateUserInput): User =
        userService.updateUser(input)

    @DgsMutation
    fun deleteUser(@InputArgument id: Long): Boolean =
        userService.deleteUser(id)
}