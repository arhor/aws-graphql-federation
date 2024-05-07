package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.aws.graphql.federation.posts.data.repository.PostRepository
import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.service.events.PostEventEmitter
import com.github.arhor.aws.graphql.federation.posts.service.mapping.OptionsMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.TagMapper
import com.github.arhor.aws.graphql.federation.posts.util.limit
import com.github.arhor.aws.graphql.federation.posts.util.offset
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Optional

internal class PostServiceImplTest {

    private val postMapper = mockk<PostMapper>()
    private val postRepository = mockk<PostRepository>()
    private val postEventEmitter = mockk<PostEventEmitter>()
    private val tagMapper = mockk<TagMapper>()
    private val tagRepository = mockk<TagRepository>()
    private val optionsMapper = mockk<OptionsMapper>()

    private val postService = PostServiceImpl(
        postMapper,
        postRepository,
        postEventEmitter,
        tagMapper,
        tagRepository,
        optionsMapper,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(
            postMapper,
            postRepository,
            postEventEmitter,
            tagMapper,
            tagRepository,
            optionsMapper,
        )
    }

    @Nested
    @DisplayName("PostService :: getPostById")
    inner class GetPostByIdTest {
        @Test
        fun `should return expected Post when PostEntity exists by passed ID`() {
            // Given
            val expectedEntity = PostEntity(
                id = 1L,
                userId = 2L,
                header = "test-header",
                content = "test-content",
            )
            val expectedPost = Post(
                id = expectedEntity.id!!,
                userId = expectedEntity.userId,
                header = expectedEntity.header,
                content = expectedEntity.content,
            )

            every { postRepository.findById(any()) } returns Optional.of(expectedEntity)
            every { postMapper.mapToPost(any<PostEntity>()) } returns expectedPost

            // When
            val result = postService.getPostById(expectedPost.id)

            // Then
            verify(exactly = 1) { postRepository.findById(expectedPost.id) }
            verify(exactly = 1) { postMapper.mapToPost(expectedEntity) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedPost)
        }

        @Test
        fun `should throw EntityNotFoundException when PostEntity does not exist by passed ID`() {
            // Given
            val postId = 1L

            val expectedEntity = DgsConstants.POST.TYPE_NAME
            val expectedCondition = "${DgsConstants.POST.Id} = $postId"
            val expectedOperation = Operation.LOOKUP

            every { postRepository.findById(any()) } returns Optional.empty()

            // When
            val result = catchException { postService.getPostById(postId) }

            // Then
            verify(exactly = 1) { postRepository.findById(postId) }

            assertThat(result)
                .isInstanceOf(EntityNotFoundException::class.java)
                .asInstanceOf(InstanceOfAssertFactories.type(EntityNotFoundException::class.java))
                .returns(expectedEntity, from { it.entity })
                .returns(expectedCondition, from { it.condition })
                .returns(expectedOperation, from { it.operation })
        }
    }

    @Nested
    @DisplayName("PostService :: getPosts")
    inner class GetPostsTest {
        @Test
        fun `should expected list when posts exist calling PostMapper for each PostEntity`() {
            // Given
            val input = PostsLookupInput()

            val expectedDataFromDB = listOf(
                PostProjection(
                    id = 1L,
                    userId = 2L,
                    header = "test-header",
                    content = "test-content",
                    options = PostEntity.Options(),
                )
            )
            val expectedPosts = expectedDataFromDB.map {
                Post(
                    id = it.id,
                    userId = it.userId,
                    header = it.header,
                    content = it.content,
                )
            }

            every { postRepository.findAll(limit = any(), offset = any()) } returns expectedDataFromDB
            every { postMapper.mapToPost(any<PostProjection>()) } returns expectedPosts.single()

            // When
            val result = postService.getPosts(input)

            // Then
            verify(exactly = 1) { postRepository.findAll(limit = input.limit, offset = input.offset) }
            verify(exactly = 1) { postMapper.mapToPost(expectedDataFromDB.single()) }

            assertThat(result)
                .isEqualTo(expectedPosts)
        }

        @Test
        fun `should return empty list when no posts found without calls to PostMapper`() {
            // Given
            val input = PostsLookupInput()

            every { postRepository.findAll(limit = any(), offset = any()) } returns emptyList()

            // When
            val result = postService.getPosts(input)

            // Then
            verify(exactly = 1) { postRepository.findAll(limit = input.limit, offset = input.offset) }

            assertThat(result)
                .isEmpty()
        }
    }
}