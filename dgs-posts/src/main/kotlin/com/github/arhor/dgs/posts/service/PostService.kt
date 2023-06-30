package com.github.arhor.dgs.posts.service

import com.github.arhor.dgs.posts.generated.graphql.types.Post
import com.github.arhor.dgs.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.dgs.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.dgs.posts.generated.graphql.types.UpdatePostInput

interface PostService {
    fun createPost(input: CreatePostInput): Post
    fun updatePost(input: UpdatePostInput): Post
    fun deletePost(id: Long): Boolean
    fun getPostById(id: Long): Post
    fun getPosts(input: PostsLookupInput): List<Post>
    fun getPostsByUserIds(userIds: Set<Long>): Map<Long, List<Post>>
}