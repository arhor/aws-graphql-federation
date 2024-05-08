package com.github.arhor.aws.graphql.federation.users.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.users.service.AuthService
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.UUID
import java.util.concurrent.Executors

internal class AuthBatchLoaderTest {

    private val executor = Executors.newSingleThreadExecutor()
    private val authService = mockk<AuthService>()

    private val authBatchLoader = AuthBatchLoader(
        executor,
        authService,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(authService)
    }

    @Test
    fun `should return completed future with empty map when empty set of keys provided`() {
        // Given
        val keys = emptySet<UUID>()

        // When
        val result = authBatchLoader.load(keys)

        // Then
        assertThat(result)
            .isNotNull()
            .isCompletedWithValue(emptyMap())
    }

    @Test
    fun `should return expected result calling getAuthoritiesByUserIds exactly once with expected keys`() {
        // Given
        val keys = sequence { yield(UUID.randomUUID()) }.take(3).toSet()
        val expectedPayload = keys.associateWith { listOf("test-$it") }

        every { authService.getAuthoritiesByUserIds(any()) } returns expectedPayload

        // When
        val result = authBatchLoader.load(keys)

        // Then
        assertThat(result)
            .isNotNull()
            .succeedsWithin(Duration.ofSeconds(1))
            .returns(expectedPayload, from { it })

        verify(exactly = 1) { authService.getAuthoritiesByUserIds(keys) }
    }
}
