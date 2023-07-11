package com.github.arhor.dgs.users.api.graphql.datafetcher

import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationResult
import com.github.arhor.dgs.users.service.AuthService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class AuthFetcher @Autowired constructor(
    private val authService: AuthService,
) {

    /* ---------- Mutations ---------- */

    @DgsMutation
    fun authenticate(@InputArgument input: AuthenticationInput): AuthenticationResult =
        authService.authenticate(input)
}
