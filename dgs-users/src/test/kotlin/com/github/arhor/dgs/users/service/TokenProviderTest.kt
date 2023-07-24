package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.service.security.toRsaPublicKey
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import kotlin.time.Duration.Companion.minutes


@SpringJUnitConfig
internal class TokenProviderTest {

    @Configuration
    @ComponentScan(
        includeFilters = [
            Filter(type = ASSIGNABLE_TYPE, classes = [TokenProvider::class])
        ],
        useDefaultFilters = false,
    )
    class Config

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Test
    fun `should pass`() {
        // Given
        val rsaPubKey = tokenProvider.activePublicKey().toRsaPublicKey()
        val jwtParser = Jwts.parserBuilder().setSigningKey(rsaPubKey).build()

        // When
        val signedJwt = tokenProvider.createSignedJwt()
        val parsedJwt = jwtParser.parse(signedJwt)

        // Then
        println(signedJwt)
        println(parsedJwt)
    }

    companion object {
        private val JWT_VALIDITY_DURATION = 10.minutes

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("app-props.jwt.expire") { JWT_VALIDITY_DURATION.toIsoString() }
            }
        }
    }
}
