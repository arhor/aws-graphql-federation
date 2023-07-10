package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.github.arhor.dgs.users.service.AuthenticationService
import com.github.arhor.dgs.users.service.Jwt
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.time.Duration.Companion.minutes

@Service
class AuthenticationServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationService {

    private val secret =
        Keys.hmacShaKeyFor("2VXAh+LCSh9lzKV/7djiYzeqjjV05JjuLoXJNOZv6M4pzERH+sGEC4VJXqoQSbIhtUBlOs5rYFR+limfmtu3TvwMFj/BrN2qHOvXUXbr1v0=".toByteArray())
    private val expire =
        10.minutes.inWholeMilliseconds

    override fun authenticate(input: AuthenticationInput): Jwt {
        val user = userRepository.findByUsername(input.username)
        if (user != null) {
            val passwordIsCorrect = passwordEncoder.matches(input.password, user.password)
            if (passwordIsCorrect) {
                val dateFrom = System.currentTimeMillis()
                val dateTill = dateFrom + expire

                return Jwt {
                    Jwts.builder()
                        .setIssuedAt(Date(dateFrom))
                        .setExpiration(Date(dateTill))
                        .claim(CLAIM_ID, user.id)
                        .claim(CLAIM_AUTHORITIES, listOf(ROLE_USER))
                        .signWith(secret)
                        .compact()
                }
            } else {
                throw RuntimeException("Password is incorrect")
            }
        } else {
            throw RuntimeException("User not found!")
        }
    }

    companion object {
        private const val CLAIM_ID = "id"
        private const val CLAIM_AUTHORITIES = "authorities"
        private const val ROLE_USER = "ROLE_USER"
    }
}
