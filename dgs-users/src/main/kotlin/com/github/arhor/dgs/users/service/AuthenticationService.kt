package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput

interface AuthenticationService {
    fun authenticate(input: AuthenticationInput): Jwt
}
