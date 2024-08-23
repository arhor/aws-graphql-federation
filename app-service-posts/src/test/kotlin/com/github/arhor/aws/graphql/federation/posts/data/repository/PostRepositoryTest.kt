package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.common.toSet
import com.github.arhor.aws.graphql.federation.posts.data.model.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.model.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.model.TagRef
import com.github.arhor.aws.graphql.federation.posts.data.model.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.model.callback.PostEntityCallback
import com.github.arhor.aws.graphql.federation.posts.data.model.callback.TagEntityCallback
import com.github.arhor.aws.graphql.federation.posts.data.repository.mapping.PostEntityCustomRowMapper
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [
        PostEntityCallback::class,
        PostEntityCustomRowMapper::class,
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
    @DisplayName("Method findPageByTagsContaining")
    inner class FindPageByTagsContainingTest {
        @Test
        fun `should return list containing expected posts data`() {
            // Given
            val user = createUser()
            val tags = createTags("test-1", "test-2", "test-3")

            val post1 = createPost(user, listOf(tags[0]), 1)
            val post2 = createPost(user, listOf(tags[1]), 2)
            val post3 = createPost(user, listOf(tags[2]), 3)
            val post4 = createPost(user, tags, 4)

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
    @DisplayName("Method countByTagsContaining")
    inner class CountByTagsContainingTest {
        @Test
        fun `should return one when there is only one post exists containing all required tags`() {
            // Given
            val user = createUser()
            val tags = createTags("test-1", "test-2", "test-3")

            createPost(user, listOf(tags[0]), 1)
            createPost(user, listOf(tags[1]), 2)
            createPost(user, listOf(tags[2]), 3)
            createPost(user, tags, 4)

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
    @DisplayName("Method findAllByUserIdIn")
    inner class FindAllByUserIdInTest {
        @Test
        fun `should return list containing expected posts data`() {
            // Given
            val user = createUser()
            val tags = createTags("test-1", "test-2", "test-3")

            val post1 = createPost(user, listOf(tags[0]), 1)
            val post2 = createPost(user, listOf(tags[1]), 2)
            val post3 = createPost(user, listOf(tags[2]), 3)
            val post4 = createPost(user, tags, 4)

            val expectedPosts = listOf(post1, post2, post3, post4)

            // When
            val result =
                postRepository
                    .findAllByUserIdIn(expectedPosts.map { it.userId!! })
                    .use { it.toList() }

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
            userRepository.save(
                UserRepresentation(
                    id = USER_ID,
                    shouldBePersisted = true,
                )
            ).also(::createPosts)

            // When
            val result = postRepository.findAllByUserIdIn(listOf(OMNI_UUID_VAL))

            // Then
            assertThat(result)
                .isNotNull()
                .isEmpty()
        }
    }

    private fun createUser() = userRepository.save(
        UserRepresentation(
            id = USER_ID,
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

    companion object {
        private val USER_ID = ZERO_UUID_VAL
    }
}
