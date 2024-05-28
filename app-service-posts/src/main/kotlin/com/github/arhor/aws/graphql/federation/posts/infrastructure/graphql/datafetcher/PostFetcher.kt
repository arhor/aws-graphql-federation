package com.github.arhor.aws.graphql.federation.posts.infrastructure.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.DeletePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.infrastructure.graphql.dataloader.PostBatchLoader
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Trace
@DgsComponent
class PostFetcher(
    private val postService: PostService,
) {

    /* ---------- Queries ---------- */

    @DgsQuery
    fun post(@InputArgument id: UUID): Post =
        postService.getPostById(id)

    @DgsQuery
    fun posts(@InputArgument input: PostsLookupInput): PostPage =
        postService.getPostPage(input)

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Posts)
    fun userPosts(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Post>> {
        val loader = dfe.getDataLoader<UUID, List<Post>>(PostBatchLoader::class.java)
        val source = dfe.getSource<User>()

        return loader.load(source.id)
    }

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun createPost(@InputArgument input: CreatePostInput): Post =
        postService.createPost(input)

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun updatePost(@InputArgument input: UpdatePostInput): Post =
        postService.updatePost(input)

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun deletePost(@InputArgument input: DeletePostInput): Boolean =
        postService.deletePost(input)
}
