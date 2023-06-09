package com.github.arhor.dgs.topics.service.impl

import com.github.arhor.dgs.topics.data.repository.PostRepository
import com.github.arhor.dgs.topics.generated.graphql.types.CreatePostRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Post
import com.github.arhor.dgs.topics.service.PostService
import com.github.arhor.dgs.topics.service.mapper.PostMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val postMapper: PostMapper,
) : PostService {

    @Transactional
    override fun createNewPost(request: CreatePostRequest): Post {
        return postMapper.mapToEntity(request)
            .let { postRepository.save(it) }
            .let { postMapper.mapToDTO(it) }
    }

    @Transactional(readOnly = true)
    override fun getPostsByUserIds(userIds: Set<String>): Map<String, List<Post>> {
        return if (userIds.isEmpty()) {
            emptyMap()
        } else {
            postRepository
                .findAllByUserIdIn(userIds)
                .map { postMapper.mapToDTO(it) }
                .groupBy { it.userId!! }
        }
    }

    override fun getPostsByTopicIds(topicIds: Set<String>): Map<String, List<Post>> {
        return if (topicIds.isEmpty()) {
            emptyMap()
        } else {
            postRepository
                .findAllByTopicIdIn(topicIds)
                .map { postMapper.mapToDTO(it) }
                .groupBy { it.topicId }
        }
    }

    override fun getPostsUserId(userId: String): List<Post> {
        return postRepository.findAllByUserId(userId).map { postMapper.mapToDTO(it) }
    }

    override fun getPostsByTopicId(topicId: String): List<Post> {
        return postRepository.findAllByTopicId(topicId).map { postMapper.mapToDTO(it) }
    }
}
