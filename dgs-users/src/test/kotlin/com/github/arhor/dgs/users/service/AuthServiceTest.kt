package com.github.arhor.dgs.users.service

import ch.qos.logback.classic.Level.ERROR
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.AuthenticationInput
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
internal class AuthServiceTest {

    @Configuration
    @ComponentScan(
        includeFilters = [
            Filter(type = ASSIGNABLE_TYPE, classes = [AuthService::class])
        ],
        useDefaultFilters = false,
    )
    class Config

    @MockkBean
    private lateinit var tokenProvider: TokenProvider

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
        val expectedAccessToken = "test.access.token"

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
        every { tokenProvider.createSignedJwt(any()) } returns expectedAccessToken

        // When
        val result = authServiceUnderTest.authenticate(input)

        // Then
        verify(exactly = 1) { userRepository.findByUsername(expectedUsername) }
        verify(exactly = 1) { passwordEncoder.matches(expectedPassword, expectedPassword) }
        verify(exactly = 1) { tokenProvider.createSignedJwt(any()) }

        confirmVerified(userRepository, passwordEncoder, tokenProvider)

        assertThat(result)
            .returns(expectedAccessToken, from { it.accessToken })
    }

    @Test
    fun `should throw DgsBadRequestException when input username is incorrect`() {
        capturingLoggingEvents {
            // Given
            val expectedUsername = "test-username"
            val expectedPassword = "test-password"

            val input = mockk<AuthenticationInput> {
                every { username } returns expectedUsername
                every { password } returns expectedPassword
            }

            every { userRepository.findByUsername(any()) } returns null

            // When
            val result = catchException { authServiceUnderTest.authenticate(input) }

            // Then
            verify(exactly = 1) { userRepository.findByUsername(expectedUsername) }

            confirmVerified(userRepository, passwordEncoder, tokenProvider)

            assertThat(result)
                .isInstanceOf(DgsBadRequestException::class.java)
                .hasMessageContaining("Bad Credentials")

            assertThat(events)
                .singleElement()
                .satisfies(
                    { assertThat(it.level).isEqualTo(ERROR) },
                    { assertThat(it.message).contains("incorrect username") },
                )
        }
    }

    @Test
    fun `should throw DgsBadRequestException when input password is incorrect`() {
        capturingLoggingEvents {
            // Given
            val expectedId = -1L
            val expectedUsername = "test-username"
            val expectedPassword = "test-password"

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
            every { passwordEncoder.matches(any(), any()) } returns false

            // When
            val result = catchException { authServiceUnderTest.authenticate(input) }

            // Then
            verify(exactly = 1) { userRepository.findByUsername(expectedUsername) }
            verify(exactly = 1) { passwordEncoder.matches(expectedPassword, expectedPassword) }

            confirmVerified(userRepository, passwordEncoder, tokenProvider)

            assertThat(result)
                .isInstanceOf(DgsBadRequestException::class.java)
                .hasMessageContaining("Bad Credentials")

            assertThat(events)
                .singleElement()
                .satisfies(
                    { assertThat(it.level).isEqualTo(ERROR) },
                    { assertThat(it.message).contains("incorrect password") },
                )
        }
    }

    private inline fun capturingLoggingEvents(body: CapturingContext.() -> Unit) {
        val logger = LoggerFactory.getLogger(ROOT_LOGGER_NAME) as Logger
        val appender = ListAppender<ILoggingEvent>()
        try {
            appender.start()
            logger.addAppender(appender)
            body(
                object : CapturingContext {
                    override val events: List<ILoggingEvent> get() = appender.list
                }
            )
        } finally {
            logger.detachAppender(appender)
            appender.stop()
        }
    }

    private interface CapturingContext {
        val events: List<ILoggingEvent>
    }
}
