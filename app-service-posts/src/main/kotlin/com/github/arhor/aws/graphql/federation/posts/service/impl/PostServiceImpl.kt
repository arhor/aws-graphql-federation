package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.common.toSet
import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation.Feature
import com.github.arhor.aws.graphql.federation.posts.data.repository.PostRepository
import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.DeletePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import com.github.arhor.aws.graphql.federation.starter.security.ensureSecuredAccess
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
            postRepository
                .findAllByUserIdIn(userIds)
                .groupBy({ it.userId!! }, postMapper::mapToPost)
        }

        else -> emptyMap()
    }

    @Transactional
    override fun createPost(input: CreatePostInput, actor: CurrentUserDetails): Post {
        val currentOperation = Operation.CREATE

        ensureUserPostsEnabled(actor.id, currentOperation)

        return postMapper.mapToEntity(input = input, userId = actor.id, tags = materialize(input.tags?.map { it.name }))
            .let(postRepository::save)
            .also { appEventPublisher.publishEvent(PostEvent.Created(id = it.id!!)) }
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

        ensureSecuredAccess(actor, initialState.userId)
        ensureUserPostsEnabled(initialState.userId!!, currentOperation)

        val currentState = initialState.copy(
            title = input.title ?: initialState.title,
            content = input.content ?: initialState.content,
            tags = input.tags?.map { it.name }?.let(::materialize)?.toSet(TagRef::from) ?: initialState.tags
        )
        return postMapper.mapToPost(
            entity = when (currentState != initialState) {
                true -> trySaveHandlingConcurrentUpdates(currentState)
                else -> initialState
            }
        )
    }

    @Transactional
    override fun deletePost(input: DeletePostInput, actor: CurrentUserDetails): Boolean {
        val post = postRepository.findByIdOrNull(input.id)
            ?: return false

        ensureSecuredAccess(actor, post.userId)

        postRepository.delete(post)
        appEventPublisher.publishEvent(PostEvent.Deleted(id = post.id!!))

        return true
    }

    private fun findPostsPageWithoutFilters(input: PostsLookupInput): PostPage {
        return postRepository
            .findAll(PageRequest.of(input.page, input.size))
            .let { postMapper.mapToPostPageFromEntity(it) }
    }

    private fun findPostsPageByTags(input: PostsLookupInput): PostPage {
        val tagNames = input.tags!!.toSet { it.name }
        val pageable = PageRequest.of(input.page, input.size)

        return postRepository.findPageByTagsContaining(tagNames, pageable.pageSize, pageable.offset).use { stream ->
            val data = stream.toList()
            val page = PageImpl(data, pageable, postRepository.countByTagsContaining(tagNames))

            postMapper.mapToPostPageFromProjection(page)
        }
    }

    private fun materialize(tags: List<String>?): Set<TagEntity> =
        when {
            !tags.isNullOrEmpty() -> {
                val presentTags = tagRepository.findAllByNameIn(tags)
                val missingTags = (tags - presentTags.toSet { it.name }).map { TagEntity(name = it) }
                val createdTags = tagRepository.saveAll(missingTags)

                HashSet<TagEntity>(presentTags.size + createdTags.size).apply {
                    addAll(presentTags)
                    addAll(createdTags)
                }
            }

            else -> emptySet()
        }

    private fun ensureUserPostsEnabled(userId: UUID, operation: Operation) {
        val user =
            userRepository.findByIdOrNull(userId)
                ?: throw EntityNotFoundException(
                    entity = POST.TYPE_NAME,
                    condition = "${USER.TYPE_NAME} with ${USER.Id} = $userId is not found",
                    operation = operation,
                )

        if (user.features.check(Feature.POSTS_DISABLED)) {
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

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
