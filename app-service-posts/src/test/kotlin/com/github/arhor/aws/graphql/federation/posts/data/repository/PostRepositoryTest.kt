package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class PostRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var postRepository: PostRepository

    @Test
    fun `should return list containing expected posts data`() {
        // Given
        val expectedPosts = createPosts().map { it.toProjection() }

        // When
        val result = postRepository.findAll(limit = 10, offset = 0)

        // Then
        assertThat(result)
            .isNotNull()
            .isNotEmpty()
            .containsExactlyInAnyOrderElementsOf(expectedPosts)
    }

    @Test
    fun `should return empty list when limit is zero`() {
        // Given
        createPosts()

        // When
        val result = postRepository.findAll(limit = 0, offset = 0)

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty()
    }

    @Test
    fun `should return empty list when offset is greater then number of existing posts`() {
        // Given
        val createdPosts = createPosts()

        // When
        val result = postRepository.findAll(limit = 10, offset = createdPosts.size.toLong())

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty()
    }

    private fun createPosts(num: Long = 3): List<PostEntity> {
        check(num >= 1) { "Number of posts to create must be greater than zero" }
        return postRepository.saveAll(
            (1..num).map {
                PostEntity(
                    userId = it,
                    header = "header-$it",
                    content = "content-$it",
                )
            }
        )
    }

    private fun PostEntity.toProjection() = PostProjection(
        id = id!!,
        userId = userId,
        header = header,
        content = content,
        options = options,
    )
}
