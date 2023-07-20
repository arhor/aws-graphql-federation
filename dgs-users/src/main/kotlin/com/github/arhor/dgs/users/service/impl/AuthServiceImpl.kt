package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationResult
import com.github.arhor.dgs.users.service.AuthService
import com.github.arhor.dgs.users.service.TokenProvider
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val tokenProvider: TokenProvider,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthService {

    override fun authenticate(input: AuthenticationInput): AuthenticationResult {
        val user = userRepository.findByUsername(input.username)
        if (user != null) {
            if (passwordEncoder.matches(input.password, user.password)) {
                val signedJwt =
                    tokenProvider.createSignedJwt(
                        identity = user.id.toString(),
                        params = mapOf(CLAIM_AUTHORITIES to listOf(ROLE_USER))
                    )
                return AuthenticationResult(accessToken = signedJwt)
            } else {
                logger.error("Provided incorrect password for the user with id: {}", user.id)
            }
        } else {
            logger.error("Cannot find user with username: {}", input.username)
        }
        throw DgsBadRequestException(message = "Bad Credentials")
    }

    companion object {
        const val CLAIM_AUTHORITIES = "authorities"
        const val ROLE_USER = "ROLE_USER"

        private val logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)
    }
}
