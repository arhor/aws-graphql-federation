package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput
import java.util.UUID

interface PostService {
    fun getPostById(id: UUID): Post
    fun getPosts(input: PostsLookupInput): List<Post>
    fun getPostsByUserIds(userIds: Set<UUID>): Map<UUID, List<Post>>
    fun createPost(input: CreatePostInput): Post
    fun updatePost(input: UpdatePostInput): Post
    fun deletePost(id: UUID): Boolean
}
