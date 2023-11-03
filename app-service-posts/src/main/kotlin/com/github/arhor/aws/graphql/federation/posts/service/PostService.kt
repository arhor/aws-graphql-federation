package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput

interface PostService {
    fun getPostById(id: Long): Post
    fun getPosts(input: PostsLookupInput): List<Post>
    fun getPostsByUserIds(userIds: Set<Long>): Map<Long, List<Post>>
    fun createPost(input: CreatePostInput): Post
    fun updatePost(input: UpdatePostInput): Post
    fun deletePost(id: Long): Boolean
    fun unlinkPostsFromUser(userId: Long)
}
