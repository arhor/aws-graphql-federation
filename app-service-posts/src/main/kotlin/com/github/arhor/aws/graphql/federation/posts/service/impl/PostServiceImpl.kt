package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.common.toSet
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.PostRepository
import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.posts.service.events.PostEventEmitter
import com.github.arhor.aws.graphql.federation.posts.service.mapping.OptionsMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.TagMapper
import com.github.arhor.aws.graphql.federation.posts.util.limit
import com.github.arhor.aws.graphql.federation.posts.util.offset
import com.github.arhor.aws.graphql.federation.tracing.Trace
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Trace
@Service
class PostServiceImpl(
    private val postMapper: PostMapper,
    private val postRepository: PostRepository,
    private val postEventEmitter: PostEventEmitter,
    private val tagMapper: TagMapper,
    private val tagRepository: TagRepository,
    private val optionsMapper: OptionsMapper,
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
    override fun getPosts(input: PostsLookupInput): List<Post> {
        return postRepository
            .findAll(limit = input.limit, offset = input.offset)
            .map(postMapper::mapToPost)
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
    override fun createPost(input: CreatePostInput): Post {
        return postMapper.mapToEntity(input = input, tags = materialize(input.tags))
            .let(postRepository::save)
            .let(postMapper::mapToPost)
    }

    @Transactional
    @Retryable(retryFor = [OptimisticLockingFailureException::class])
    override fun updatePost(input: UpdatePostInput): Post {
        val initialState = postRepository.findByIdOrNull(input.id) ?: throw EntityNotFoundException(
            entity = POST.TYPE_NAME,
            condition = "${POST.Id} = ${input.id}",
            operation = Operation.UPDATE,
        )
        val currentState = initialState.copy(
            header = input.header ?: initialState.header,
            content = input.content ?: initialState.content,
            options = input.options?.let(optionsMapper::mapFromList) ?: initialState.options,
            tags = input.tags?.let(::materialize)?.let(tagMapper::mapToRefs) ?: initialState.tags
        )

        return postMapper.mapToPost(
            entity = when (currentState != initialState) {
                true -> postRepository.save(currentState)
                else -> initialState
            }
        )
    }

    @Transactional
    override fun deletePost(id: UUID): Boolean {
        return when (val post = postRepository.findByIdOrNull(id)) {
            null -> false
            else -> {
                postRepository.delete(post)
                postEventEmitter.emit(PostEvent.Deleted(id = id))
                true
            }
        }
    }

    private fun materialize(tags: List<String>?): Set<TagEntity> = when {
        tags != null -> {
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
}
