package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.common.toSet
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
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
    private lateinit var userRepository: UserRepresentationRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Nested
    @DisplayName("PostRepository :: findPageByTagsContaining")
    inner class FindPageByTagsContainingTest {
        @Test
        fun `should return list containing expected posts data`() {
            // Given
            val user = createUser()
            val tags = createTags("test-1", "test-2", "test-3")

            val post1 = createPost(user, listOf(tags[0]), 1).toProjection()
            val post2 = createPost(user, listOf(tags[1]), 2).toProjection()
            val post3 = createPost(user, listOf(tags[2]), 3).toProjection()
            val post4 = createPost(user, tags, 4).toProjection()

            // When
            val result =
                postRepository
                    .findPageByTagsContaining(tags.toSet { it.name }, 20, 0)
                    .use { it.toList() }

            // Then
            assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .doesNotContain(post1, post2, post3)
                .containsExactly(post4)
        }

        @Test
        fun `should return empty list when tags passed as empty list`() {
            // Given
            val user = createUser()
            createPosts(user)

            // When
            val result =
                postRepository
                    .findPageByTagsContaining(tags = emptySet(), limit = 20, offset = 0)
                    .use { it.toList() }

            // Then
            assertThat(result)
                .isNotNull()
                .isEmpty()
        }
    }

    @Nested
    @DisplayName("PostRepository :: countByTagsContaining")
    inner class CountByTagsContainingTest {
        @Test
        fun `should return one when there is only one post exists containing all required tags`() {
            // Given
            val user = createUser()
            val tags = createTags("test-1", "test-2", "test-3")

            createPost(user, listOf(tags[0]), 1).toProjection()
            createPost(user, listOf(tags[1]), 2).toProjection()
            createPost(user, listOf(tags[2]), 3).toProjection()
            createPost(user, tags, 4).toProjection()

            // When
            val result = postRepository.countByTagsContaining(tags.toSet { it.name })

            // Then
            assertThat(result)
                .isOne()
        }

        @Test
        fun `should return zero when empty set of tags passed`() {
            // Given
            val user = createUser()
            createPosts(user)

            // When
            val result = postRepository.countByTagsContaining(tags = emptySet())

            // Then
            assertThat(result)
                .isZero()
        }
    }

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
                userRepository.save(UserRepresentation(id = UUID.randomUUID(), shouldBePersisted = true))
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

    private fun createUser() = userRepository.save(
        UserRepresentation(
            id = UUID.randomUUID(),
            shouldBePersisted = true,
        )
    )

    private fun createTags(vararg names: String) =
        names.takeIf { it.isNotEmpty() }
            ?.map { TagEntity(name = it) }
            ?.let { tagRepository.saveAll(it) }
            ?: emptyList()

    private fun createPost(
        user: UserRepresentation,
        tags: List<TagEntity> = emptyList(),
        num: Long
    ) = postRepository.save(
        PostEntity(
            userId = user.id,
            title = "title-$num",
            content = "content-$num",
            tags = tags.toSet(TagRef::from)
        )
    )

    private fun createPosts(
        user: UserRepresentation,
        tags: List<TagEntity> = emptyList(),
        num: Long = 3,
    ) = postRepository.saveAll(
        (1..num).map {
            PostEntity(
                userId = user.id,
                title = "title-$it",
                content = "content-$it",
                tags = tags.toSet(TagRef::from)
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
