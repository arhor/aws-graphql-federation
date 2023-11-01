package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.users.service.TokenProvider
import com.github.arhor.aws.graphql.federation.users.service.security.convertToRsaPublicKey
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import kotlin.time.Duration.Companion.minutes

@SpringJUnitConfig
internal class TokenProviderTest {

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Test
    fun `should create jwt with expected validity duration`() {
        // given
        val rsaPubKey = tokenProvider.activePublicKey().convertToRsaPublicKey()
        val jwtParser = Jwts.parserBuilder().setSigningKey(rsaPubKey).build()

        // when
        val signedJwt = tokenProvider.createSignedJwt()
        val parsedJwt = jwtParser.parse(signedJwt)
        val actualValidity = with(parsedJwt.body as Claims) { expiration.time - issuedAt.time }

        // then
        assertThat(actualValidity)
            .isEqualTo(JWT_VALIDITY_DURATION.inWholeMilliseconds)
    }

    @Configuration
    @ComponentScan(
        includeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [TokenProvider::class])],
        useDefaultFilters = false,
    )
    class Config

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
