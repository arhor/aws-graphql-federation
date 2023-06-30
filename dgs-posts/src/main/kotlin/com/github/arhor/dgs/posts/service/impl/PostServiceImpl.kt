package com.github.arhor.dgs.posts.service.impl

import com.github.arhor.dgs.lib.exception.EntityNotFoundException
import com.github.arhor.dgs.lib.exception.Operation
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
import com.github.arhor.dgs.posts.service.PostMapper
import com.github.arhor.dgs.posts.service.PostService
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PostServiceImpl(
    private val postMapper: PostMapper,
    private val postRepository: PostRepository,
    private val bannerImageRepository: BannerImageRepository,
    private val tagRepository: TagRepository,
) : PostService {

    @Transactional(readOnly = true)
    override fun getPostById(id: Long): Post {
        return postRepository.findByIdOrNull(id)?.let(postMapper::mapToDTO)
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
            .map(postMapper::mapToDTO)
            .toList()
    }

    @Transactional(readOnly = true)
    override fun getPostsByUserIds(userIds: Set<Long>): Map<Long, List<Post>> = when {
        userIds.isNotEmpty() -> {
            postRepository
                .findAllByUserIdIn(userIds)
                .groupBy({ it.userId!! }, postMapper::mapToDTO)
        }

        else -> emptyMap()
    }

    @Transactional
    override fun createPost(input: CreatePostInput): Post {
        val tagRefs = materialize(input.tags)
        val bannerFilename = input.banner?.let { "${input.userId}__${UUID.randomUUID()}__${it.name}" }

        val post =
            postMapper.mapToEntity(dto = input, banner = bannerFilename, tags = tagRefs)
                .let(postRepository::save)
                .let(postMapper::mapToDTO)

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
        var currentState = initialState

        input.header?.let {
            currentState = currentState.copy(header = it)
        }
        input.content?.let {
            currentState = currentState.copy(content = it)
        }
        input.options?.let {
            currentState = currentState.copy(options = postMapper.wrapOptions(it))
        }
        input.tags?.let {
            currentState = currentState.copy(tags = materialize(it))
        }

        return postMapper.mapToDTO(
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
                true
            }
        }
    }

    @Transactional
    override fun unlinkPostsFromUser(userId: Long) {
        val unlinkedPosts =
            postRepository.findAllByUserId(userId).use { data ->
                data.map { it.copy(userId = null) }
                    .toList()
            }
        if (unlinkedPosts.isNotEmpty()) {
            postRepository.saveAll(unlinkedPosts)
        }
    }

    /**
     * Persists missing tags to the database, returning tag references.
     */
    private fun materialize(tags: List<String>?): Set<TagRef> = when {
        tags != null -> {
            val presentTags = tagRepository.findAllByNameIn(tags)
            val missingTags = (tags - presentTags.mapTo(HashSet()) { it.name }).map(TagEntity::create)
            val createdTags = tagRepository.saveAll(missingTags)

            val initialCapacity = presentTags.size + createdTags.size

            sequenceOf(presentTags, createdTags)
                .flatten()
                .mapTo(HashSet(initialCapacity), TagRef::create)
        }

        else -> emptySet()
    }
}
