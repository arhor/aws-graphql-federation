package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.service.security.convertToRsaPublicKey
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.longs.shouldBeExactly
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import kotlin.time.Duration.Companion.minutes

internal class TokenProviderTest(
    @Autowired
    private val tokenProvider: TokenProvider
) : DescribeSpec() {

    @Configuration
    @ComponentScan(
        includeFilters = [Filter(type = ASSIGNABLE_TYPE, classes = [TokenProvider::class])],
        useDefaultFilters = false,
    )
    class Config

    companion object {
        private val JWT_VALIDITY_DURATION = 10.minutes

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("app-props.jwt.expire", JWT_VALIDITY_DURATION::toIsoString)
            }
        }
    }

    init {
        it("should create jwt with expected validity duration") {
            // Given
            val rsaPubKey = tokenProvider.activePublicKey().convertToRsaPublicKey()
            val jwtParser = Jwts.parserBuilder().setSigningKey(rsaPubKey).build()

            // When
            val signedJwt = tokenProvider.createSignedJwt()
            val parsedJwt = jwtParser.parse(signedJwt)

            // Then
            with(parsedJwt.body as Claims) {
                (expiration.time - issuedAt.time) shouldBeExactly JWT_VALIDITY_DURATION.inWholeMilliseconds
            }
        }
    }
}
