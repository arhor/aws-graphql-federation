package com.github.arhor.dgs.comments.graphql.fetcher

import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.ARTICLE
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.comments.generated.graphql.types.Article
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentRequest
import com.github.arhor.dgs.comments.generated.graphql.types.Indentifiable
import com.github.arhor.dgs.comments.generated.graphql.types.User
import com.github.arhor.dgs.comments.graphql.loader.CommentBatchLoader
import com.github.arhor.dgs.comments.service.CommentService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsEntityFetcher
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.concurrent.CompletableFuture

@DgsComponent
class CommentFetcher(private val commentService: CommentService) {

    @DgsMutation
    fun createComment(@InputArgument request: CreateCommentRequest) =
        commentService.createComment(request)

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Comments)
    fun userComments(dfe: DgsDataFetchingEnvironment) =
        dfe.loadCommentsUsing<CommentBatchLoader.ForUser>()

    @DgsData(parentType = ARTICLE.TYPE_NAME, field = ARTICLE.Comments)
    fun articleComments(dfe: DgsDataFetchingEnvironment) =
        dfe.loadCommentsUsing<CommentBatchLoader.ForArticle>()

    @DgsEntityFetcher(name = ARTICLE.TYPE_NAME)
    fun fetchArticle(values: Map<String, Any>) =
        Article(id = values[ARTICLE.Id]!!.toString())

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun fetchUser(values: Map<String, Any>) =
        User(id = values[USER.Id]!!.toString())

    private inline fun <reified T> DgsDataFetchingEnvironment.loadCommentsUsing(): CompletableFuture<List<Comment>>
        where T : CommentBatchLoader {

        val loader = getDataLoader<Long, List<Comment>>(T::class.java)
        val source = getSource<Indentifiable>()

        return loader.load(source.id.toLong())
    }
}
