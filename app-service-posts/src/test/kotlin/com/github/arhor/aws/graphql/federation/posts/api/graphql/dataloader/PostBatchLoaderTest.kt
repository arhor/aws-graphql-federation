package com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.Executors

internal class PostBatchLoaderTest {

    private val executor = Executors.newSingleThreadExecutor()
    private val postService = mockk<PostService>()

    private val postBatchLoader = PostBatchLoader(
        executor,
        postService,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(postService)
    }

    @Test
    fun `should return completed future with empty map when empty set of keys provided`() {
        // Given
        val keys = emptySet<Long>()

        // When
        val result = postBatchLoader.load(keys)

        // Then
        assertThat(result)
            .isNotNull()
            .isCompletedWithValue(emptyMap())
    }

    @Test
    fun `should return expected result calling getPostsByUserIds exactly once with expected keys`() {
        // Given
        val keys = setOf<Long>(1, 2, 3)
        val expectedPayload = keys.associateWith { listOf(Post(id = it, header = "test", content = "test")) }

        every { postService.getPostsByUserIds(any()) } returns expectedPayload

        // When
        val result = postBatchLoader.load(keys)

        // Then
        assertThat(result)
            .isNotNull()
            .succeedsWithin(Duration.ofSeconds(1))
            .returns(expectedPayload, from { it })

        verify(exactly = 1) { postService.getPostsByUserIds(keys) }
    }
}
