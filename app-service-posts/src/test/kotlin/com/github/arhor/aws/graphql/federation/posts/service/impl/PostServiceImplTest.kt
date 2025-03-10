package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.common.toSet
import com.github.arhor.aws.graphql.federation.posts.data.model.LikeRef
import com.github.arhor.aws.graphql.federation.posts.data.model.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.model.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.model.UserRepresentation.UserFeature
import com.github.arhor.aws.graphql.federation.posts.data.model.UserRepresentation.UserFeatures
import com.github.arhor.aws.graphql.federation.posts.data.repository.PostRepository
import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.data.repository.sorting.Posts
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.TagInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.type
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID
import java.util.stream.Stream

class PostServiceImplTest {

    private val appEventPublisher = mockk<ApplicationEventPublisher>()
    private val postMapper = mockk<PostMapper>()
    private val postRepository = mockk<PostRepository>()
    private val tagRepository = mockk<TagRepository>()
    private val userRepository = mockk<UserRepresentationRepository>()

    private val postService = PostServiceImpl(
        appEventPublisher,
        postMapper,
        postRepository,
        tagRepository,
        userRepository,
    )

    @AfterEach
    fun `confirm that all interactions with mocked dependencies were verified`() {
        confirmVerified(
            postMapper,
            postRepository,
            appEventPublisher,
            tagRepository,
        )
    }

    @Nested
    @DisplayName("Method getPostById")
    inner class GetPostByIdTest {
        @Test
        fun `should return expected Post when PostEntity exists by passed ID`() {
            // Given
            val expectedEntity = createPostEntity()
            val expectedPost = expectedEntity.toPost()

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
            val expectedEntity = POST.TYPE_NAME
            val expectedCondition = "${POST.Id} = $POST_1_ID"
            val expectedOperation = Operation.LOOKUP

            every { postRepository.findById(any()) } returns Optional.empty()

            // When
            val result = catchException { postService.getPostById(POST_1_ID) }

            // Then
            verify(exactly = 1) { postRepository.findById(POST_1_ID) }

            assertThat(result)
                .asInstanceOf(type(EntityNotFoundException::class.java))
                .returns(expectedEntity, from { it.entity })
                .returns(expectedCondition, from { it.condition })
                .returns(expectedOperation, from { it.operation })
        }
    }

    @Nested
    @DisplayName("Method getPostPage")
    inner class GetPostsTest {
        @Test
        fun `should expected page when posts exist without any filters`() {
            // Given
            val input = PostsLookupInput()
            val dataFromDB = listOf(createPostEntity())
            val expectedPosts = dataFromDB.map { it.toPost() }
            val expectedPage = PageImpl(dataFromDB)
            val expectedRequest = PageRequest.of(input.page, input.size, Posts.sortedByCreatedDateTimeDesc)

            every { postRepository.findAll(any<Pageable>()) } returns expectedPage
            every { postMapper.mapToPostPageFromEntity(any()) } returns PostPage(data = expectedPosts)

            // When
            val result = postService.getPostPage(input)

            // Then
            verify(exactly = 1) { postRepository.findAll(expectedRequest) }
            verify(exactly = 1) { postMapper.mapToPostPageFromEntity(expectedPage) }

            assertThat(result.data)
                .isEqualTo(expectedPosts)
        }

        @Test
        fun `should return empty page when no posts found without any filters`() {
            // Given
            val input = PostsLookupInput()
            val empty = Page.empty<PostEntity>()
            val expectedRequest = PageRequest.of(input.page, input.size, Posts.sortedByCreatedDateTimeDesc)

            every { postRepository.findAll(any<Pageable>()) } returns empty
            every { postMapper.mapToPostPageFromEntity(any()) } returns PostPage(data = emptyList())

            // When
            val result = postService.getPostPage(input)

            // Then
            verify(exactly = 1) { postRepository.findAll(expectedRequest) }
            verify(exactly = 1) { postMapper.mapToPostPageFromEntity(empty) }

            assertThat(result.data)
                .isEmpty()
        }

        @Test
        fun `should expected page when posts exist with provided set of tags`() {
            // Given
            val tags = setOf("test-1", "test-2")
            val input = PostsLookupInput(tags = tags.map { TagInput(it) })
            val dataFromDB = listOf(createPostEntity())
            val expectedPosts = dataFromDB.map { it.toPost() }
            val request = PageRequest.of(input.page, input.size)
            val expectedPage = PageImpl(dataFromDB, request, Long.MAX_VALUE)

            every { postRepository.findPageByTagsContaining(any(), any(), any()) } answers { dataFromDB.stream() }
            every { postRepository.countByTagsContaining(any()) } returns Long.MAX_VALUE
            every { postMapper.mapToPostPageFromEntity(any()) } returns PostPage(data = expectedPosts)

            // When
            val result = postService.getPostPage(input)

            // Then
            verify(exactly = 1) { postRepository.findPageByTagsContaining(tags, request.pageSize, request.offset) }
            verify(exactly = 1) { postRepository.countByTagsContaining(tags) }
            verify(exactly = 1) { postMapper.mapToPostPageFromEntity(expectedPage) }

            assertThat(result.data)
                .isEqualTo(expectedPosts)
        }

        @Test
        fun `should return empty page when no posts found with provided set of tags`() {
            // Given
            val tags = setOf("test-1", "test-2")
            val input = PostsLookupInput(tags = tags.map { TagInput(it) })
            val request = PageRequest.of(input.page, input.size)
            val empty = PageImpl(emptyList<PostEntity>(), request, Long.MAX_VALUE)

            every { postRepository.findPageByTagsContaining(any(), any(), any()) } returns Stream.empty()
            every { postRepository.countByTagsContaining(any()) } returns Long.MAX_VALUE
            every { postMapper.mapToPostPageFromEntity(any()) } returns PostPage(data = emptyList())

            // When
            val result = postService.getPostPage(input)

            // Then
            verify(exactly = 1) { postRepository.findPageByTagsContaining(tags, request.pageSize, request.offset) }
            verify(exactly = 1) { postRepository.countByTagsContaining(tags) }
            verify(exactly = 1) { postMapper.mapToPostPageFromEntity(empty) }

            assertThat(result.data)
                .isEmpty()
        }
    }

    @Nested
    @DisplayName("Method getPostsByUserIds")
    inner class GetPostsByUserIdsTest {
        @Test
        fun `should return expected posts grouped by user id when they exist in the repository`() {
            // Given
            val post1Projection = createPostEntity(id = POST_1_ID)
            val post2Projection = createPostEntity(id = POST_2_ID)

            val projections = listOf(post1Projection, post2Projection)
            val posts = projections.map { it.toPost() }

            val expectedUserIds = projections.toSet { it.userId!! }
            val expectedResult = posts.groupBy { it.userId }

            every { postRepository.findAllByUserIdIn(any()) } answers { projections.stream() }
            every { postMapper.mapToPost(any()) } returnsMany posts

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

    @Nested
    @DisplayName("Method createPost")
    inner class CreatePostTest {
        @Test
        fun `should successfully create new post publishing PostEvent#Created to the application`() {
            // Given
            val input = CreatePostInput(
                title = "test-title",
                content = "test-content",
            )
            val post = createPostEntity()
            val user = UserRepresentation(USER_ID)
            val expectedPost = post.toPost()

            every { userRepository.findById(any()) } returns Optional.of(user)
            every { postMapper.mapToEntity(any(), any(), any()) } returns post
            every { postRepository.save(any()) } answers { firstArg() }
            every { appEventPublisher.publishEvent(any<Any>()) } just runs
            every { postMapper.mapToPost(any<PostEntity>()) } returns expectedPost

            // When
            val result = postService.createPost(input, mockk {
                every { id } returns USER_ID
                every { authorities } returns emptyList()
            })

            // Then
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { postMapper.mapToEntity(input, USER_ID, null) }
            verify(exactly = 1) { postRepository.save(post) }
            verify(exactly = 1) { appEventPublisher.publishEvent(PostEvent.Created(id = post.id!!, userId = USER_ID)) }
            verify(exactly = 1) { postMapper.mapToPost(post) }

            assertThat(result)
                .isNotNull()
        }

        @Test
        fun `should throw EntityNotFoundException when specified user does not exist`() {
            // Given
            val input = CreatePostInput(
                title = "test-title",
                content = "test-content",
            )

            val expectedEntity = POST.TYPE_NAME
            val expectedCondition = "${USER.TYPE_NAME} with ${USER.Id} = $USER_ID is not found"
            val expectedOperation = Operation.CREATE

            every { userRepository.findById(any()) } returns Optional.empty()

            // When
            val result = catchException {
                postService.createPost(input, mockk {
                    every { id } returns USER_ID
                    every { authorities } returns emptyList()
                })
            }

            // Then
            verify(exactly = 1) { userRepository.findById(USER_ID) }

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityNotFoundException::class.java))
                .returns(expectedEntity, from { it.entity })
                .returns(expectedCondition, from { it.condition })
                .returns(expectedOperation, from { it.operation })
        }
    }

    @Nested
    @DisplayName("Method updatePost")
    inner class UpdatePostTest {
        @Test
        fun `should throw EntityNotFoundException when specified post does not exist`() {
            // Given
            val input = UpdatePostInput(id = POST_1_ID)

            val expectedEntity = POST.TYPE_NAME
            val expectedCondition = "${POST.Id} = ${input.id}"
            val expectedOperation = Operation.UPDATE

            every { postRepository.findById(any()) } returns Optional.empty()

            // When
            val result = catchException {
                postService.updatePost(input, mockk {
                    every { id } returns USER_ID
                    every { authorities } returns emptyList()
                })
            }

            // Then
            verify(exactly = 1) { postRepository.findById(input.id) }

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityNotFoundException::class.java))
                .returns(expectedEntity, from { it.entity })
                .returns(expectedCondition, from { it.condition })
                .returns(expectedOperation, from { it.operation })
        }

        @Test
        fun `should throw EntityNotFoundException when user does not exist`() {
            // Given
            val input = UpdatePostInput(id = POST_1_ID)
            val post = createPostEntity()

            val expectedEntity = POST.TYPE_NAME
            val expectedCondition = "${USER.TYPE_NAME} with ${USER.Id} = $USER_ID is not found"
            val expectedOperation = Operation.UPDATE

            every { postRepository.findById(any()) } returns Optional.of(post)
            every { userRepository.findById(any()) } returns Optional.empty()

            // When
            val result = catchException {
                postService.updatePost(input, mockk {
                    every { id } returns USER_ID
                    every { authorities } returns emptyList()
                })
            }

            // Then
            verify(exactly = 1) { postRepository.findById(input.id) }
            verify(exactly = 1) { userRepository.findById(USER_ID) }

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityNotFoundException::class.java))
                .returns(expectedEntity, from { it.entity })
                .returns(expectedCondition, from { it.condition })
                .returns(expectedOperation, from { it.operation })
        }

        @Test
        fun `should throw EntityOperationRestrictedException when user posts disabled`() {
            // Given
            val input = UpdatePostInput(id = POST_1_ID)
            val post = createPostEntity()
            val user = UserRepresentation(id = USER_ID, features = UserFeatures(UserFeature.POSTS_DISABLED))

            val expectedEntity = POST.TYPE_NAME
            val expectedCondition = "Posts disabled for the ${USER.TYPE_NAME} with ${USER.Id} = $USER_ID"
            val expectedOperation = Operation.UPDATE

            every { postRepository.findById(any()) } returns Optional.of(post)
            every { userRepository.findById(any()) } returns Optional.of(user)

            // When
            val result = catchException {
                postService.updatePost(input, mockk {
                    every { id } returns USER_ID
                    every { authorities } returns emptyList()
                })
            }

            // Then
            verify(exactly = 1) { postRepository.findById(input.id) }
            verify(exactly = 1) { userRepository.findById(USER_ID) }

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityOperationRestrictedException::class.java))
                .returns(expectedEntity, from { it.entity })
                .returns(expectedCondition, from { it.condition })
                .returns(expectedOperation, from { it.operation })
        }

        @Test
        fun `should not call PostRepository#save when there are no updates done to the entity`() {
            // Given
            val input = UpdatePostInput(id = POST_1_ID)
            val post = createPostEntity()
            val user = UserRepresentation(USER_ID)
            val expectedPost = post.toPost()

            every { postRepository.findById(any()) } returns Optional.of(post)
            every { userRepository.findById(any()) } returns Optional.of(user)
            every { postMapper.mapToPost(any<PostEntity>()) } returns expectedPost

            // When
            val result = postService.updatePost(input, mockk {
                every { id } returns USER_ID
                every { authorities } returns emptyList()
            })

            // Then
            verify(exactly = 1) { postRepository.findById(POST_1_ID) }
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { postMapper.mapToPost(post) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedPost)
        }

        @Test
        fun `should call PostRepository#save when there are updates done to the entity`() {
            // Given
            val input = UpdatePostInput(
                id = POST_1_ID,
                title = "new-test-title",
                content = "new-test-content",
                tags = emptyList(),
            )
            val post = createPostEntity()
            val user = UserRepresentation(USER_ID)
            val updatedPost = post.copy(title = input.title!!, content = input.content!!)
            val expectedPost = updatedPost.toPost()

            every { postRepository.findById(any()) } returns Optional.of(post)
            every { userRepository.findById(any()) } returns Optional.of(user)
            every { postRepository.save(any()) } answers { firstArg() }
            every { postMapper.mapToPost(any<PostEntity>()) } returns expectedPost

            // When
            val result = postService.updatePost(input, mockk {
                every { id } returns USER_ID
                every { authorities } returns emptyList()
            })

            // Then
            verify(exactly = 1) { postRepository.findById(POST_1_ID) }
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { postRepository.save(updatedPost) }
            verify(exactly = 1) { postMapper.mapToPost(updatedPost) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedPost)
        }

        @Test
        fun `should throw EntityOperationRestrictedException when concurrent modification of the post occurred`() {
            // Given
            val input = UpdatePostInput(
                id = POST_1_ID,
                title = "new-test-title",
                content = "new-test-content",
                tags = emptyList(),
            )
            val post = createPostEntity()
            val user = UserRepresentation(USER_ID)
            val updatedPost = post.copy(title = input.title!!, content = input.content!!)

            val expectedEntity = POST.TYPE_NAME
            val expectedCondition = "${POST.Id} = ${post.id} (updated concurrently)"
            val expectedOperation = Operation.UPDATE

            every { postRepository.findById(any()) } returns Optional.of(post)
            every { userRepository.findById(any()) } returns Optional.of(user)
            every { postRepository.save(any()) } throws OptimisticLockingFailureException("test")

            // When
            val result = catchException {
                postService.updatePost(input, mockk {
                    every { id } returns USER_ID
                    every { authorities } returns emptyList()
                })
            }

            // Then
            verify(exactly = 1) { postRepository.findById(input.id) }
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { postRepository.save(updatedPost) }

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityOperationRestrictedException::class.java))
                .returns(expectedEntity, from { it.entity })
                .returns(expectedCondition, from { it.condition })
                .returns(expectedOperation, from { it.operation })
        }
    }

    @Nested
    @DisplayName("Method deletePost")
    inner class DeletePostTest {
        @Test
        fun `should successfully delete existing post publishing PostEvent#Deleted to the application`() {
            // Given
            val entity = createPostEntity()

            every { userRepository.findById(any()) } returns Optional.of(UserRepresentation(id = USER_ID))
            every { postRepository.findById(any()) } returns Optional.of(entity)
            every { postRepository.delete(any()) } just runs
            every { appEventPublisher.publishEvent(any<PostEvent>()) } just runs

            // When
            val result = postService.deletePost(entity.id!!, mockk {
                every { id } returns USER_ID
                every { authorities } returns emptyList()
            })

            // Then
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { postRepository.findById(entity.id!!) }
            verify(exactly = 1) { postRepository.delete(entity) }
            verify(exactly = 1) { appEventPublisher.publishEvent(PostEvent.Deleted(id = entity.id!!)) }

            assertThat(result)
                .isTrue()
        }

        @Test
        fun `should return result with success false trying to delete post which does not exist`() {
            // Given
            every { postRepository.findById(any()) } returns Optional.empty()

            // When
            val result = postService.deletePost(POST_1_ID, mockk {
                every { id } returns USER_ID
                every { authorities } returns emptyList()
            })

            // Then
            verify(exactly = 1) { postRepository.findById(POST_1_ID) }

            assertThat(result)
                .isFalse()
        }
    }

    @Nested
    @DisplayName("Method togglePostLike")
    inner class TogglePostLikeTest {
        @Test
        fun `should create like on the post from the given acting user`() {
            // Given
            val post = createPostEntity()
            val user = UserRepresentation(USER_ID)

            every { postRepository.findById(any()) } returns Optional.of(post)
            every { userRepository.findById(any()) } returns Optional.of(user)
            every { postRepository.save(any()) } answers { firstArg() }

            // When
            val result = postService.togglePostLike(POST_1_ID, USER_ID)

            // Then
            verify(exactly = 1) { postRepository.findById(POST_1_ID) }
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { postRepository.save(any()) }

            assertThat(result)
                .isTrue()
        }

        @Test
        fun `should delete like on the post from the given acting user`() {
            // Given
            val user = UserRepresentation(USER_ID)
            val like = LikeRef.from(user)
            val post = createPostEntity().run { copy(likes = likes + like) }

            every { postRepository.findById(any()) } returns Optional.of(post)
            every { userRepository.findById(any()) } returns Optional.of(user)
            every { postRepository.save(any()) } answers { firstArg() }

            // When
            val result = postService.togglePostLike(POST_1_ID, USER_ID)

            // Then
            verify(exactly = 1) { postRepository.findById(POST_1_ID) }
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { postRepository.save(any()) }

            assertThat(result)
                .isFalse()
        }
    }

    private fun createPostEntity(id: UUID = POST_1_ID) = PostEntity(
        id = id,
        userId = USER_ID,
        title = "test-title",
        content = "test-content",
    )

    private fun PostEntity.toPost() = Post(
        id = id!!,
        userId = userId,
        title = title,
        content = content,
    )

    companion object {
        private val USER_ID = ZERO_UUID_VAL
        private val POST_1_ID = TEST_1_UUID_VAL
        private val POST_2_ID = TEST_2_UUID_VAL
    }
}
