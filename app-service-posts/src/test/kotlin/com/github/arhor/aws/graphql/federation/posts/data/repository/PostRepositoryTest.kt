package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.entity.callback.PostEntityCallback
import com.github.arhor.aws.graphql.federation.posts.data.entity.callback.TagEntityCallback
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.util.UUID

@ContextConfiguration(
    classes = [
        PostEntityCallback::class,
        TagEntityCallback::class,
    ]
)
class PostRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var userRepresentationRepository: UserRepresentationRepository

    @Nested
    @DisplayName("PostRepository :: findAllByUserIdIn")
    inner class FindAllByUserIdInTest {
        @Test
        fun `should return list containing expected posts data`() {
            // Given
            val user = createUser()
            val expectedPosts = createPosts(user).map { it.toProjection() }

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
            val user = createUser()
            createPosts(user)

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
            val user =
                userRepresentationRepository.save(UserRepresentation(id = UUID.randomUUID(), shouldBePersisted = true))
            createPosts(user)
            val incorrectUserIds = listOf(UUID.randomUUID())

            // When
            val result = postRepository.findAllByUserIdIn(incorrectUserIds)

            // Then
            assertThat(result)
                .isNotNull()
                .isEmpty()
        }
    }

    private fun createUser() = userRepresentationRepository.save(
        UserRepresentation(
            id = UUID.randomUUID(),
            shouldBePersisted = true,
        )
    )

    private fun createPosts(user: UserRepresentation, num: Long = 3) = postRepository.saveAll(
        (1..num).map {
            PostEntity(
                userId = user.id,
                title = "title-$it",
                content = "content-$it",
            )
        }
    )

    private fun PostEntity.toProjection() = PostProjection(
        id = id!!,
        userId = userId,
        title = title,
        content = content,
        options = options,
    )
}
