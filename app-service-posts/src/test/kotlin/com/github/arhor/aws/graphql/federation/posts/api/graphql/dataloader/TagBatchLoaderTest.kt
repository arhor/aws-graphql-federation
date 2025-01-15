package com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.posts.service.TagService
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_3_UUID_VAL
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

class TagBatchLoaderTest {

    private val executor = Executors.newSingleThreadExecutor()
    private val tagService = mockk<TagService>()

    private val tagBatchLoader = TagBatchLoader(
        executor,
        tagService,
    )

    @AfterEach
    fun `confirm that all interactions with mocked dependencies were verified`() {
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
        val keys = setOf(TEST_1_UUID_VAL, TEST_2_UUID_VAL, TEST_3_UUID_VAL)
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
