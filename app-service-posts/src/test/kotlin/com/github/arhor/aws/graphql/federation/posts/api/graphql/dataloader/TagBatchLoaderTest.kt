package com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.posts.service.TagService
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

internal class TagBatchLoaderTest {

    private val executor = Executors.newSingleThreadExecutor()
    private val tagService = mockk<TagService>()

    private val tagBatchLoader = TagBatchLoader(
        executor,
        tagService,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(tagService)
    }

    @Test
    fun `should return completed future with empty map when empty set of keys provided`() {
        // Given
        val keys = emptySet<UUID>()

        // When
        val result = tagBatchLoader.load(keys)

        // Then
        assertThat(result)
            .isNotNull()
            .isCompletedWithValue(emptyMap())
    }

    @Test
    fun `should return expected result calling getTagsByPostIds exactly once with expected keys`() {
        // Given
        val keys = (1..3).map { UUID.randomUUID() }.toSet()
        val expectedPayload = keys.associateWith { listOf("test-$it") }

        every { tagService.getTagsByPostIds(any()) } returns expectedPayload

        // When
        val result = tagBatchLoader.load(keys)

        // Then
        assertThat(result)
            .isNotNull()
            .succeedsWithin(Duration.ofSeconds(1))
            .returns(expectedPayload, from { it })

        verify(exactly = 1) { tagService.getTagsByPostIds(keys) }
    }
}
