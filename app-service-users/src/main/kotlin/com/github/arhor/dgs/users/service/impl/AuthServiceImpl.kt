package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationResult
import com.github.arhor.dgs.users.service.AuthService
import com.github.arhor.dgs.users.service.TokenProvider
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException
import io.jsonwebtoken.Claims
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    private val tokenProvider: TokenProvider,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthService {

    @Transactional(readOnly = true)
    override fun authenticate(input: AuthenticationInput): AuthenticationResult {
        val user = userRepository.findByUsername(input.username)
        if (user != null) {
            if (passwordEncoder.matches(input.password, user.password)) {
                val signedJwt =
                    tokenProvider.createSignedJwt {
                        claim(CLAIM_SUBJECT, user.id.toString())
                        claim(CLAIM_AUTHORITIES, listOf(ROLE_USER))
                    }
                return AuthenticationResult(accessToken = signedJwt)
            } else {
                logger.error("Provided incorrect password for the user with id: {}", user.id)
            }
        } else {
            logger.error("Provided incorrect username: {}", input.username)
        }
        throw DgsBadRequestException(message = "Bad Credentials")
    }

    companion object {
        const val CLAIM_SUBJECT = Claims.SUBJECT
        const val CLAIM_AUTHORITIES = "authorities"
        const val ROLE_USER = "ROLE_USER"

        private val logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)
    }
}
