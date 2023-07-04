package com.github.arhor.dgs.comments.service

import com.github.arhor.dgs.comments.generated.graphql.types.Post

interface PostService {
    fun getPostById(postId: Long): Post
    fun createPost(postId: Long)
    fun deletePost(postId: Long)
}
