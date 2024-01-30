@file:Suppress("ClassName", "SameParameterValue")

package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.retry.annotation.EnableRetry
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.util.stream.Stream

@SpringJUnitConfig
internal class AuthServiceTest {

    @EnableRetry
    @Configuration
    @ComponentScan(
        includeFilters = [Filter(type = ASSIGNABLE_TYPE, classes = [AuthService::class])],
        useDefaultFilters = false,
    )
    class Config

    @MockkBean
    private lateinit var authRepository: AuthRepository

    @Autowired
    private lateinit var authService: AuthService

    @MethodSource
    @ParameterizedTest
    fun `should invoke method getAuthoritiesByUserIds expected times`(
        // given
        expectedUserIds: Set<Long>,
        expectedInvocations: Int,
    ) {
        every { authRepository.findAllByUserIdIn(any()) } returns emptyMap()

        // when
        authService.getAuthoritiesByUserIds(expectedUserIds)

        // then
        verify(exactly = expectedInvocations) { authRepository.findAllByUserIdIn(expectedUserIds) }
    }

    companion object {
        @JvmStatic
        fun `should invoke method getAuthoritiesByUserIds expected times`(): Stream<Arguments> = Stream.of(
            // @formatter:off
            arguments(setOf(1L)       , 1),
            arguments(setOf(1L, 2L)   , 1),
            arguments(emptySet<Long>(), 0),
            // @formatter:on
        )
    }
}
