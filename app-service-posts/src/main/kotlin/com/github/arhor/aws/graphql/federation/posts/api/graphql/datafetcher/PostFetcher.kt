package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader.PostBatchLoader
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
        val source = dfe.getSource<User>() ?: return CompletableFuture.completedFuture(null)
        val loader = dfe.getDataLoader<UUID, List<Post>>(PostBatchLoader::class.java)

        return loader.load(source.id)
    }

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun createPost(@InputArgument input: CreatePostInput, @AuthenticationPrincipal actor: CurrentUserDetails): Post =
        postService.createPost(input, actor)

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun updatePost(@InputArgument input: UpdatePostInput, @AuthenticationPrincipal actor: CurrentUserDetails): Post =
        postService.updatePost(input, actor)

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun deletePost(@InputArgument id: UUID, @AuthenticationPrincipal actor: CurrentUserDetails): Boolean =
        postService.deletePost(id, actor)

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun togglePostLike(@InputArgument postId: UUID, @AuthenticationPrincipal actor: CurrentUserDetails): Boolean =
        postService.togglePostLike(postId, actor.id)
}
