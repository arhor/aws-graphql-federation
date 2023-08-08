package com.github.arhor.dgs.posts.api.graphql.datafetcher

import com.github.arhor.dgs.posts.api.graphql.dataloader.PostBatchLoader
import com.github.arhor.dgs.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.dgs.posts.generated.graphql.types.Post
import com.github.arhor.dgs.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.dgs.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.dgs.posts.generated.graphql.types.User
import com.github.arhor.dgs.posts.service.PostService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture

@DgsComponent
class PostFetcher @Autowired constructor(
    private val postService: PostService,
) {
    /* Queries */

    @DgsQuery
    fun post(@InputArgument id: Long): Post =
        postService.getPostById(id)

    @DgsQuery
    fun posts(@InputArgument input: PostsLookupInput): List<Post> =
        postService.getPosts(input)

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Posts)
    fun userPosts(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Post>> {
        val loader = dfe.getDataLoader<Long, List<Post>>(PostBatchLoader::class.java)
        val source = dfe.getSource<User>()

        return loader.load(source.id)
    }

    /* Mutations */

    @DgsMutation
    fun createPost(@InputArgument input: CreatePostInput): Post =
        postService.createPost(input)

    @DgsMutation
    fun updatePost(@InputArgument input: UpdatePostInput): Post =
        postService.updatePost(input)

    @DgsMutation
    fun deletePost(@InputArgument id: Long): Boolean =
        postService.deletePost(id)
}
