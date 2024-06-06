package com.github.arhor.aws.graphql.federation.users.infrastructure.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import com.github.arhor.aws.graphql.federation.starter.security.PredefinedAuthority.ROLE_ADMIN
import com.github.arhor.aws.graphql.federation.starter.security.ensureSecuredAccess
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UserPage
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    fun users(@InputArgument input: UsersLookupInput): UserPage =
        userService.getUserPage(input)

    /* ---------- Mutations ---------- */

    @DgsMutation
    fun createUser(@InputArgument input: CreateUserInput): User =
        userService.createUser(input)

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun updateUser(
        @InputArgument input: UpdateUserInput,
        @AuthenticationPrincipal actor: CurrentUserDetails,
    ): User {
        ensureSecuredAccess(actor, input.id)
        return userService.updateUser(input)
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun deleteUser(
        @InputArgument input: DeleteUserInput,
        @AuthenticationPrincipal actor: CurrentUserDetails,
    ): Boolean {
        ensureSecuredAccess(actor, input.id, ROLE_ADMIN)
        return userService.deleteUser(input)
    }
}
