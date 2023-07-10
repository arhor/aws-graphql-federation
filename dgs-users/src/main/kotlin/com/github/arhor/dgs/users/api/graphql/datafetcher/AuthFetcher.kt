package com.github.arhor.dgs.users.api.graphql.datafetcher

import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationResult
import com.github.arhor.dgs.users.service.AuthenticationService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class AuthFetcher @Autowired constructor(
    private val authService: AuthenticationService,
) {

    /* Mutations */

    @DgsMutation
    fun authenticate(@InputArgument input: AuthenticationInput): AuthenticationResult =
        AuthenticationResult(accessToken = authService.authenticate(input).value)
}
