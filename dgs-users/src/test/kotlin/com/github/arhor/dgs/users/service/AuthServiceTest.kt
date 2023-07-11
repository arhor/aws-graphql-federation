@file:Suppress("ClassName", "SameParameterValue")

package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.github.arhor.dgs.users.service.impl.AuthServiceImpl.Companion.CLAIM_AUTHORITIES
import com.github.arhor.dgs.users.service.impl.AuthServiceImpl.Companion.ROLE_USER
import com.ninjasquad.springmockk.MockkBean
import io.jsonwebtoken.Jwts
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
internal class AuthServiceTest {

    @Configuration
    @ComponentScan(
        useDefaultFilters = false, includeFilters = [
            Filter(type = ASSIGNABLE_TYPE, classes = [AuthService::class])
        ]
    )
    class Config

    @MockkBean
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var authServiceUnderTest: AuthService

    @Test
    fun `should return result with access token for the valid username and password pair`() {
        // Given
        val expectedId = -1L
        val expectedUsername = "test-username"
        val expectedPassword = "test-password"
        val expectedAuthorities = listOf(ROLE_USER)

        val input = mockk<AuthenticationInput> {
            every { username } returns expectedUsername
            every { password } returns expectedPassword
        }

        val user = mockk<UserEntity> {
            every { id } returns expectedId
            every { username } returns expectedUsername
            every { password } returns expectedPassword
        }

        every { userRepository.findByUsername(any()) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true

        // When
        val result = authServiceUnderTest.authenticate(input)

        // Then
        val jwt = jwtParser.parseClaimsJws(result.accessToken)

        assertThat(jwt)
            .returns(expectedId, from { it.body.subject.toLong() })
            .returns(expectedAuthorities, from { it.body[CLAIM_AUTHORITIES] })
    }

    companion object {
        private const val SECRET =
            "2VXAh+LCSh9lzKV/7djiYzeqjjV05JjuLoXJNOZv6M4pzERH+sGEC4VJXqoQSbIhtUBlOs5rYFR+limfmtu3TvwMFj/BrN2qHOvXUXbr1v0="
        private const val EXPIRE =
            "30m"

        private val jwtParser =
            Jwts.parserBuilder()
                .setSigningKey(SECRET.toByteArray())
                .build()

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("app-props.jwt.secret") { SECRET }
                add("app-props.jwt.expire") { EXPIRE }
            }
        }
    }
}
