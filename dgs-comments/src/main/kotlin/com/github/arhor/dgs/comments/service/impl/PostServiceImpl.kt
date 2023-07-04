package com.github.arhor.dgs.comments.service.impl

import com.github.arhor.dgs.comments.data.entity.PostEntity
import com.github.arhor.dgs.comments.data.repository.PostRepository
import com.github.arhor.dgs.comments.generated.graphql.types.Post
import com.github.arhor.dgs.comments.service.PostService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
) : PostService {

    override fun getPostById(postId: Long): Post? {
        return postRepository.findByIdOrNull(postId)?.let { Post(id = it.id) }
    }

    @Transactional
    override fun createPost(postId: Long) {
        postRepository.insert(PostEntity(id = postId))
    }

    @Transactional
    override fun deletePost(postId: Long) {
        postRepository.deleteById(postId)
    }
}
