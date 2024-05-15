package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostResult
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.DeletePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.DeletePostResult
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostResult
import java.util.UUID

interface PostService {
    fun getPostById(id: UUID): Post
    fun getPosts(input: PostsLookupInput): List<Post>
    fun getPostsByUserIds(userIds: Set<UUID>): Map<UUID, List<Post>>
    fun createPost(input: CreatePostInput): CreatePostResult
    fun updatePost(input: UpdatePostInput): UpdatePostResult
    fun deletePost(input: DeletePostInput): DeletePostResult
}
