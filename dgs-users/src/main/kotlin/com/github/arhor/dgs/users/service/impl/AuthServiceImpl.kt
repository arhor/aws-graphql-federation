package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationResult
import com.github.arhor.dgs.users.service.AuthService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.util.Date
import kotlin.time.Duration

@Service
class AuthServiceImpl(
    @Value("\${app-props.jwt.expire}") expire: String,
    private val jwtSigningKeyPair: KeyPair,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthService {

    private val jwtExpiresIn = Duration.parse(expire).inWholeMilliseconds

    override fun authenticate(input: AuthenticationInput): AuthenticationResult =
        userRepository.findByUsername(input.username)
            ?.takeIf { passwordEncoder.matches(input.password, it.password) }
            ?.let { AuthenticationResult(accessToken = buildJwt(it)) }
            ?: throw RuntimeException("Bad Credentials")

    private fun buildJwt(user: UserEntity): String {
        val dateFrom = System.currentTimeMillis()
        val dateTill = dateFrom + jwtExpiresIn

        return Jwts.builder()
            .setIssuedAt(Date(dateFrom))
            .setExpiration(Date(dateTill))
            .setSubject(user.id.toString())
            .claim(CLAIM_AUTHORITIES, listOf(ROLE_USER))
            .signWith(jwtSigningKeyPair.private)
            .compact()
    }

    companion object {
        const val CLAIM_AUTHORITIES = "authorities"
        const val ROLE_USER = "ROLE_USER"
    }
}
