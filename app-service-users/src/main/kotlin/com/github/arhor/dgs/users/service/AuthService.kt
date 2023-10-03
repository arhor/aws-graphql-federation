package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationResult

interface AuthService {
    fun authenticate(input: AuthenticationInput): AuthenticationResult
}
