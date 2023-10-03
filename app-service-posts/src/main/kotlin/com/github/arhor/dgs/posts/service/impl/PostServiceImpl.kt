package com.github.arhor.dgs.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.dgs.posts.data.entity.TagEntity
import com.github.arhor.dgs.posts.data.entity.TagRef
import com.github.arhor.dgs.posts.data.repository.BannerImageRepository
import com.github.arhor.dgs.posts.data.repository.PostRepository
import com.github.arhor.dgs.posts.data.repository.TagRepository
import com.github.arhor.dgs.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.dgs.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.dgs.posts.generated.graphql.types.Post
import com.github.arhor.dgs.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.dgs.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.dgs.posts.service.events.PostEventEmitter
import com.github.arhor.dgs.posts.service.PostService
import com.github.arhor.dgs.posts.service.mapping.OptionsMapper
import com.github.arhor.dgs.posts.service.mapping.PostMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PostServiceImpl @Autowired constructor(
    private val postMapper: PostMapper,
    private val postRepository: PostRepository,
    private val postEventEmitter: PostEventEmitter,
    private val bannerImageRepository: BannerImageRepository,
    private val tagRepository: TagRepository,
    private val optionsMapper: OptionsMapper,
) : PostService {

    @Transactional(readOnly = true)
    override fun getPostById(id: Long): Post {
        return postRepository.findByIdOrNull(id)?.let(postMapper::map)
            ?: throw EntityNotFoundException(
                entity = POST.TYPE_NAME,
                condition = "${POST.Id} = $id",
                operation = Operation.READ,
            )
    }

    @Transactional(readOnly = true)
    override fun getPosts(input: PostsLookupInput): List<Post> {
        return postRepository
            .findAll(limit = input.size, offset = input.page * input.size)
            .map(postMapper::map)
            .toList()
    }

    @Transactional(readOnly = true)
    override fun getPostsByUserIds(userIds: Set<Long>): Map<Long, List<Post>> = when {
        userIds.isNotEmpty() -> {
            postRepository
                .findAllByUserIdIn(userIds)
                .groupBy({ it.userId!! }, postMapper::map)
        }

        else -> emptyMap()
    }

    @Transactional
    override fun createPost(input: CreatePostInput): Post {
        val tagRefs = materialize(input.tags)
        val bannerFilename = input.banner?.let { "${input.userId}__${UUID.randomUUID()}__${it.name}" }

        val post =
            postMapper.map(input = input, banner = bannerFilename, tags = tagRefs)
                .let(postRepository::save)
                .let(postMapper::map)

        if (bannerFilename != null) {
            bannerImageRepository.upload(filename = bannerFilename, data = input.banner.inputStream)
        }
        return post
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
            options = input.options?.let(optionsMapper::map) ?: initialState.options,
            tags = input.tags?.let(::materialize) ?: initialState.tags
        )

        return postMapper.map(
            entity = when (currentState != initialState) {
                true -> postRepository.save(currentState)
                else -> initialState
            }
        )
    }

    @Transactional
    override fun deletePost(id: Long): Boolean {
        return when (val post = postRepository.findByIdOrNull(id)) {
            null -> false
            else -> {
                postRepository.delete(post)
                post.banner?.let { bannerImageRepository.delete(it) }
                postEventEmitter.emit(PostEvent.Deleted(id = id))
                true
            }
        }
    }

    @Transactional
    override fun unlinkPostsFromUser(userId: Long) {
        postRepository.unlinkAllFromUser(userId)
    }

    /**
     * Persists missing tags to the database, returning tag references.
     */
    private fun materialize(tags: List<String>?): Set<TagRef> = when {
        tags != null -> {
            val presentTags = tagRepository.findAllByNameIn(tags)
            val missingTags = (tags - presentTags.mapTo(HashSet()) { it.name }).map(TagEntity::create)
            val createdTags = tagRepository.saveAll(missingTags)

            HashSet<TagRef>(presentTags.size + createdTags.size).also {
                for (tag in presentTags) {
                    it += TagRef.create(tag)
                }
                for (tag in createdTags) {
                    it += TagRef.create(tag)
                }
            }
        }

        else -> emptySet()
    }
}
