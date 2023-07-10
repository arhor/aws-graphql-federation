package com.github.arhor.dgs.comments.api.graphql.datafetcher

import com.github.arhor.dgs.comments.api.graphql.dataloader.CommentBatchLoader
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.POST
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentInput
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentResult
import com.github.arhor.dgs.comments.generated.graphql.types.Post
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentInput
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentResult
import com.github.arhor.dgs.comments.generated.graphql.types.User
import com.github.arhor.dgs.comments.service.CommentService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture

@DgsComponent
class CommentFetcher @Autowired constructor(
    private val commentService: CommentService,
) {

    /* Queries */

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Comments)
    fun userComments(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Comment>> =
        loadCommentsUsing<CommentBatchLoader.ForUser, User>(dfe, User::id)

    @DgsData(parentType = POST.TYPE_NAME, field = POST.Comments)
    fun postComments(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Comment>> =
        loadCommentsUsing<CommentBatchLoader.ForPost, Post>(dfe, Post::id)

    /* Mutations */

    @DgsMutation
    fun createComment(@InputArgument input: CreateCommentInput): CreateCommentResult =
        CreateCommentResult(comment = commentService.createComment(input))

    @DgsMutation
    fun updateComment(@InputArgument input: UpdateCommentInput): UpdateCommentResult =
        UpdateCommentResult(comment = commentService.updateComment(input))

    /* Internal implementation */

    private inline fun <reified T, D> loadCommentsUsing(
        dfe: DgsDataFetchingEnvironment,
        id: D.() -> Long
    ): CompletableFuture<List<Comment>> where T : CommentBatchLoader {

        val loader = dfe.getDataLoader<Long, List<Comment>>(T::class.java)
        val source = dfe.getSource<D>()

        return loader.load(source.id())
    }
}
