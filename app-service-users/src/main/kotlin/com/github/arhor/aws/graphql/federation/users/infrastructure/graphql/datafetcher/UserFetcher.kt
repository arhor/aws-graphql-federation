package com.github.arhor.aws.graphql.federation.users.infrastructure.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@Trace
@DgsComponent
class UserFetcher(
    private val userService: UserService,
) {

    /* ---------- Queries ---------- */

    @DgsQuery
    fun user(@InputArgument id: UUID): User =
        userService.getUserById(id)

    @DgsQuery
    fun users(@InputArgument input: UsersLookupInput): List<User> =
        userService.getAllUsers(input)

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("!isAuthenticated()")
    fun createUser(@InputArgument input: CreateUserInput): User =
        userService.createUser(input)

    @DgsMutation
    @PreAuthorize("isAuthenticated() && hasRole('USER')")
    fun updateUser(@InputArgument input: UpdateUserInput): User =
        userService.updateUser(input)

    @DgsMutation
    @PreAuthorize("isAuthenticated() && hasRole('USER')")
    fun deleteUser(@InputArgument input: DeleteUserInput): Boolean =
        userService.deleteUser(input)
}
