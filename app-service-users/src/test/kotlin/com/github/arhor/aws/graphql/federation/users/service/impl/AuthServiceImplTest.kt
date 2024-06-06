package com.github.arhor.aws.graphql.federation.users.service.impl

import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.UUID
import java.util.stream.Stream

class AuthServiceImplTest {

    private val authRepository = mockk<AuthRepository>()
    private val authService = AuthServiceImpl(authRepository)

    @MethodSource
    @ParameterizedTest
    fun `should invoke method getAuthoritiesByUserIds expected times`(
        // Given
        expectedUserIds: Set<UUID>,
        expectedInvocations: Int,
    ) {
        every { authRepository.findAllByUserIdIn(any()) } returns emptyMap()

        // When
        authService.getAuthoritiesByUserIds(expectedUserIds)

        // Then
        verify(exactly = expectedInvocations) { authRepository.findAllByUserIdIn(expectedUserIds) }
    }

    companion object {
        private val USER_1_ID = TEST_1_UUID_VAL
        private val USER_2_ID = TEST_2_UUID_VAL

        @JvmStatic
        fun `should invoke method getAuthoritiesByUserIds expected times`(): Stream<Arguments> = Stream.of(
            // @formatter:off
            arguments(setOf(USER_1_ID)           , 1),
            arguments(setOf(USER_1_ID, USER_2_ID), 1),
            arguments(emptySet<UUID>()           , 0),
            // @formatter:on
        )
    }
}
