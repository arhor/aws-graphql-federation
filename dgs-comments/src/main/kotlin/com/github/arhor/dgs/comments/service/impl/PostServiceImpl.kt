package com.github.arhor.dgs.comments.service.impl

import com.github.arhor.dgs.comments.data.repository.PostRepository
import com.github.arhor.dgs.comments.generated.graphql.types.Post
import com.github.arhor.dgs.comments.service.PostService
import org.springframework.stereotype.Service

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
) : PostService {

    override fun getPostById(postId: Long): Post {
        TODO("Not yet implemented")
    }

    override fun createPost(postId: Long) {
        TODO("Not yet implemented")
    }

    override fun deletePost(postId: Long) {
        TODO("Not yet implemented")
    }
}
