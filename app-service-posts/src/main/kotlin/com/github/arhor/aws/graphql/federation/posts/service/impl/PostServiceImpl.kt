package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.common.toSet
import com.github.arhor.aws.graphql.federation.posts.data.entity.LikeRef
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
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
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import com.github.arhor.aws.graphql.federation.starter.security.ensureAccessAllowed
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import java.util.stream.Collectors.groupingBy

@Trace
@Service
class PostServiceImpl(
    private val appEventPublisher: ApplicationEventPublisher,
    private val postMapper: PostMapper,
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepresentationRepository,
) : PostService {

    @Transactional(readOnly = true)
    override fun getPostById(id: UUID): Post {
        return postRepository.findByIdOrNull(id)?.let(postMapper::mapToPost)
            ?: throw EntityNotFoundException(
                entity = POST.TYPE_NAME,
                condition = "${POST.Id} = $id",
                operation = Operation.LOOKUP,
            )
    }

    @Transactional(readOnly = true)
    override fun getPostPage(input: PostsLookupInput): PostPage {
        return if (input.tags == null) {
            findPostsPageWithoutFilters(input)
        } else {
            findPostsPageByTags(input)
        }
    }

    @Transactional(readOnly = true)
    override fun getPostsByUserIds(userIds: Set<UUID>): Map<UUID, List<Post>> = when {
        userIds.isNotEmpty() -> {
            postRepository.findAllByUserIdIn(userIds).use { data ->
                data.map(postMapper::mapToPost)
                    .collect(groupingBy { it.userId })
            }
        }

        else -> emptyMap()
    }

    @Transactional
    override fun createPost(input: CreatePostInput, actor: CurrentUserDetails): Post {
        val currentOperation = Operation.CREATE
        val userId = actor.id

        ensureOperationAllowed(userId, currentOperation)

        return postMapper.mapToEntity(input = input, userId = userId, tags = convertToRefs(input.tags))
            .let(postRepository::save)
            .also { appEventPublisher.publishEvent(PostEvent.Created(id = it.id!!, userId = userId)) }
            .let(postMapper::mapToPost)
    }

    @Transactional
    override fun updatePost(input: UpdatePostInput, actor: CurrentUserDetails): Post {
        val currentOperation = Operation.UPDATE
        val initialState = postRepository.findByIdOrNull(input.id)
            ?: throw EntityNotFoundException(
                entity = POST.TYPE_NAME,
                condition = "${POST.Id} = ${input.id}",
                operation = currentOperation,
            )

        ensureOperationAllowed(initialState.userId!!, currentOperation, actor)

        val currentState = initialState.copy(
            title = input.title ?: initialState.title,
            content = input.content ?: initialState.content,
            tags = convertToRefs(input.tags) ?: initialState.tags
        )
        return postMapper.mapToPost(
            entity = when (currentState != initialState) {
                true -> trySaveHandlingConcurrentUpdates(currentState)
                else -> initialState
            }
        )
    }

    @Transactional
    override fun deletePost(id: UUID, actor: CurrentUserDetails): Boolean {
        val currentOperation = Operation.DELETE
        val post = postRepository.findByIdOrNull(id)
            ?: return false

        ensureOperationAllowed(post.userId!!, currentOperation, actor)

        postRepository.delete(post)
        appEventPublisher.publishEvent(PostEvent.Deleted(id = post.id!!))

        return true
    }

    @Transactional
    override fun togglePostLike(postId: UUID, actor: CurrentUserDetails): Boolean {
        val post = postRepository.findByIdOrNull(postId)
            ?: throw EntityNotFoundException(
                entity = POST.TYPE_NAME,
                condition = "${POST.Id} = $postId",
                operation = Operation.UPDATE,
            )
        val user = userRepository.findByIdOrNull(actor.id)
            ?: throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = ${actor.id}",
                operation = Operation.UPDATE,
            )
        val like = LikeRef.from(user)

        val updatedPost = postRepository.save(
            post.copy(
                likes = if (like in post.likes) {
                    post.likes - like
                } else {
                    post.likes + like
                }
            )
        )
        return like in updatedPost.likes
    }

    private fun findPostsPageWithoutFilters(input: PostsLookupInput): PostPage {
        return postRepository
            .findAll(PageRequest.of(input.page, input.size, Posts.sortedByCreatedDateTimeDesc))
            .let { postMapper.mapToPostPageFromEntity(it) }
    }

    private fun findPostsPageByTags(input: PostsLookupInput): PostPage {
        val tagNames = input.tags!!.toSet { it.name }
        val pageable = PageRequest.of(input.page, input.size)
        val totalNumberOfTaggedPosts = postRepository.countByTagsContaining(tagNames)

        return postRepository.findPageByTagsContaining(tagNames, pageable.pageSize, pageable.offset).use { stream ->
            val data = stream.toList()
            val page = PageImpl(data, pageable, totalNumberOfTaggedPosts)

            postMapper.mapToPostPageFromEntity(page)
        }
    }

    private fun convertToRefs(tags: List<TagInput>?): Set<TagRef>? =
        when {
            tags == null -> {
                null
            }

            tags.isEmpty() -> {
                emptySet()
            }

            else -> {
                val tagNames = tags.map { normalizeTag(it.name) }

                val presentTags = tagRepository.findAllByNameIn(tagNames)
                val missingTags = (tagNames - presentTags.toSet { it.name }).map { TagEntity(name = it) }
                val createdTags = tagRepository.saveAll(missingTags)

                HashSet<TagRef>(presentTags.size + createdTags.size).apply {
                    presentTags.forEach { add(TagRef.from(it)) }
                    createdTags.forEach { add(TagRef.from(it)) }
                }
            }
        }

    private fun ensureOperationAllowed(
        userId: UUID,
        operation: Operation,
        actor: CurrentUserDetails? = null,
    ) {
        if (actor != null) {
            ensureAccessAllowed(userId, actor)
        }
        ensureUserPostsEnabled(userId, operation)
    }

    private fun ensureUserPostsEnabled(userId: UUID, operation: Operation) {
        val user =
            userRepository.findByIdOrNull(userId)
                ?: throw EntityNotFoundException(
                    entity = POST.TYPE_NAME,
                    condition = "${USER.TYPE_NAME} with ${USER.Id} = $userId is not found",
                    operation = operation,
                )

        if (user.postsDisabled()) {
            throw EntityOperationRestrictedException(
                entity = POST.TYPE_NAME,
                condition = "Posts disabled for the ${USER.TYPE_NAME} with ${USER.Id} = $userId",
                operation = operation
            )
        }
    }

    private fun trySaveHandlingConcurrentUpdates(entity: PostEntity): PostEntity {
        return try {
            postRepository.save(entity)
        } catch (e: OptimisticLockingFailureException) {
            logger.error(e.message, e)

            throw EntityOperationRestrictedException(
                entity = POST.TYPE_NAME,
                condition = "${POST.Id} = ${entity.id} (updated concurrently)",
                operation = Operation.UPDATE,
                cause = e,
            )
        }
    }

    private fun normalizeTag(name: String): String =
        name.trim()
            .replace(SEQUENCE_OF_WHITESPACES, SINGLE_SPACE)
            .lowercase()

    companion object {
        private const val SINGLE_SPACE = " "
        private val SEQUENCE_OF_WHITESPACES = Regex("\\s+")
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
