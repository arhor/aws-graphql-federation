package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class PostRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var postRepository: PostRepository

    @Nested
    @DisplayName("PostRepository :: findAll")
    inner class FindAllTest {
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
    }

    @Nested
    @DisplayName("PostRepository :: findAllByUserIdIn")
    inner class FindAllByUserIdInTest {
        @Test
        fun `should return list containing expected posts data`() {
            // Given
            val expectedPosts = createPosts().map { it.toProjection() }

            // When
            val result = postRepository.findAllByUserIdIn(expectedPosts.map { it.userId!! })

            // Then
            assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expectedPosts)
        }

        @Test
        fun `should return empty list when userIds passed as empty list`() {
            // Given
            createPosts()

            // When
            val result = postRepository.findAllByUserIdIn(emptyList())

            // Then
            assertThat(result)
                .isNotNull()
                .isEmpty()
        }

        @Test
        fun `should return empty list when offset is greater then number of existing posts`() {
            // Given
            val createdPosts = createPosts()
            val lastGeneratedUserId = createdPosts.maxOf { it.userId!! }
            val incorrectUserIds = createdPosts.map { it.userId!! + lastGeneratedUserId + 1 }

            // When
            val result = postRepository.findAllByUserIdIn(incorrectUserIds)

            // Then
            assertThat(result)
                .isNotNull()
                .isEmpty()
        }
    }

    @Nested
    @DisplayName("PostRepository :: unlinkAllFromUsers")
    inner class UnlinkAllFromUsersTest {
        @Test
        fun `should updated all posts with passed user ids to set null to userId`() {
            // Given
            val createdPosts = createPosts()

            // When
            postRepository.unlinkAllFromUsers(createdPosts.map { it.userId!! })
            val updatedPosts = postRepository.findAllById(createdPosts.map { it.id })

            // Then
            assertThat(createdPosts)
                .isNotEmpty()
                .allSatisfy { assertThat(it.userId).isNotNull() }

            assertThat(updatedPosts)
                .isNotEmpty()
                .allSatisfy { assertThat(it.userId).isNull() }
        }
    }

    private fun createPosts(num: Long = 3) = postRepository.saveAll(
        (1..num).map {
            PostEntity(
                userId = it,
                header = "header-$it",
                content = "content-$it",
            )
        }
    )

    private fun PostEntity.toProjection() = PostProjection(
        id = id!!,
        userId = userId,
        header = header,
        content = content,
        options = options,
    )
}
