package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.common.toSet
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.aws.graphql.federation.posts.data.repository.PostRepository
import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
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
import org.springframework.context.ApplicationEventPublisher
import java.util.Optional
import java.util.UUID

class PostServiceImplTest {

    private val appEventPublisher = mockk<ApplicationEventPublisher>()
    private val postMapper = mockk<PostMapper>()
    private val postRepository = mockk<PostRepository>()
    private val tagMapper = mockk<TagMapper>()
    private val tagRepository = mockk<TagRepository>()
    private val optionsMapper = mockk<OptionsMapper>()

    private val postService = PostServiceImpl(
        appEventPublisher,
        postMapper,
        postRepository,
        tagMapper,
        tagRepository,
        optionsMapper,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(
            postMapper,
            postRepository,
            appEventPublisher,
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
                id = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                title = "test-title",
                content = "test-content",
            )
            val expectedPost = Post(
                id = expectedEntity.id!!,
                userId = expectedEntity.userId,
                title = expectedEntity.title,
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
            val postId = UUID.randomUUID()

            val expectedEntity = POST.TYPE_NAME
            val expectedCondition = "${POST.Id} = $postId"
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
                    id = UUID.randomUUID(),
                    userId = UUID.randomUUID(),
                    title = "test-title",
                    content = "test-content",
                    options = PostEntity.Options(),
                )
            )
            val expectedPosts = expectedDataFromDB.map {
                Post(
                    id = it.id,
                    userId = it.userId,
                    title = it.title,
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

    @Nested
    @DisplayName("PostService :: getPostsByUserIds")
    inner class GetPostsByUserIdsTest {
        @Test
        fun `should return expected posts grouped by user id when they exist in the repository`() {
            // Given
            val post1Projection = createPostProjection()
            val post2Projection = createPostProjection()

            val projections = listOf(post1Projection, post2Projection)
            val posts = projections.map { it.toPost() }

            val expectedUserIds = projections.toSet { it.userId!! }
            val expectedResult = posts.groupBy { it.userId }

            every { postRepository.findAllByUserIdIn(any()) } returns projections
            every { postMapper.mapToPost(any<PostProjection>()) } returnsMany posts

            // When
            val result = postService.getPostsByUserIds(expectedUserIds)

            // Then
            verify(exactly = 1) { postRepository.findAllByUserIdIn(expectedUserIds) }
            verify(exactly = 1) { postMapper.mapToPost(post1Projection) }
            verify(exactly = 1) { postMapper.mapToPost(post2Projection) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult)
        }

        @Test
        fun `should return empty map without repository calls when passed user ids empty`() {
            // Given
            val userIds = emptySet<UUID>()

            // When
            val result = postService.getPostsByUserIds(userIds)

            // Then
            assertThat(result)
                .isNotNull()
                .isEmpty()
        }
    }

    private fun createPostProjection() = PostProjection(
        id = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        title = "test-title",
        content = "test-content",
        options = PostEntity.Options(),
    )

    private fun PostProjection.toPost() = Post(
        id = id,
        userId = userId,
        title = title,
        content = content,
        options = options.items.toList(),
    )
}
