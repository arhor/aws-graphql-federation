package com.github.arhor.aws.graphql.federation.users.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@Trace
@DgsComponent
class UserFetcher(
    private val userService: UserService,
) {

    /* ---------- Queries ---------- */

    @DgsQuery
    fun user(@InputArgument id: Long): User =
        userService.getUserById(id)

    @DgsQuery
    fun users(@InputArgument input: UsersLookupInput): List<User> =
        userService.getAllUsers(input)

    /* ---------- Mutations ---------- */

    @DgsMutation
    fun createUser(@InputArgument input: CreateUserInput): CreateUserResult =
        userService.createUser(input)

    @DgsMutation
    fun updateUser(@InputArgument input: UpdateUserInput): UpdateUserResult =
        userService.updateUser(input)

    @DgsMutation
    fun deleteUser(@InputArgument input: DeleteUserInput): DeleteUserResult =
        userService.deleteUser(input)
}
