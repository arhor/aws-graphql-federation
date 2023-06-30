package com.github.arhor.dgs.comments.graphql.fetcher

import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.POST
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentRequest
import com.github.arhor.dgs.comments.generated.graphql.types.Indentifiable
import com.github.arhor.dgs.comments.generated.graphql.types.Post
import com.github.arhor.dgs.comments.generated.graphql.types.User
import com.github.arhor.dgs.comments.graphql.loader.CommentBatchLoader
import com.github.arhor.dgs.comments.service.CommentService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsEntityFetcher
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.math.BigInteger
import java.util.concurrent.CompletableFuture

@DgsComponent
class CommentFetcher(private val commentService: CommentService) {

    @DgsMutation
    fun createComment(@InputArgument request: CreateCommentRequest) =
        commentService.createComment(request)

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Comments)
    fun userComments(dfe: DgsDataFetchingEnvironment) =
        dfe.loadCommentsUsing<CommentBatchLoader.ForUser>()

    @DgsData(parentType = POST.TYPE_NAME, field = POST.Comments)
    fun articleComments(dfe: DgsDataFetchingEnvironment) =
        dfe.loadCommentsUsing<CommentBatchLoader.ForPost>()

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    fun fetchPost(values: Map<String, Any>): Post =
        Post(id = (values[POST.Id] as BigInteger).longValueExact())

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun fetchUser(values: Map<String, Any>): User =
        User(id = (values[USER.Id] as BigInteger).longValueExact())

    private inline fun <reified T> DgsDataFetchingEnvironment.loadCommentsUsing(): CompletableFuture<List<Comment>>
        where T : CommentBatchLoader {

        val loader = getDataLoader<Long, List<Comment>>(T::class.java)
        val source = getSource<Indentifiable>()

        return loader.load(source.id.toLong())
    }
}
