package com.github.arhor.dgs.comments.graphql.loader

import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.service.CommentService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Function

sealed class CommentBatchLoader(
    private val executor: Executor,
    private val loadingFunction: Function<Set<String>, Map<String, List<Comment>>>,
) : MappedBatchLoader<String, List<Comment>> {

    override fun load(keys: Set<String>): CompletableFuture<Map<String, List<Comment>>> {
        return CompletableFuture.supplyAsync({ loadingFunction.apply(keys) }, executor)
    }

    @DgsDataLoader(name = USER_COMMENTS, maxBatchSize = 50)
    class ForUser(asyncExecutor: Executor, commentService: CommentService) : CommentBatchLoader(
        executor = asyncExecutor,
        loadingFunction = commentService::getCommentsByUserIds
    )

    @DgsDataLoader(name = ARTICLE_COMMENTS, maxBatchSize = 50)
    class ForArticle(asyncExecutor: Executor, commentService: CommentService) : CommentBatchLoader(
        executor = asyncExecutor,
        loadingFunction = commentService::getCommentsByTopicIds
    )

    companion object {
        const val USER_COMMENTS = "userComments"
        const val ARTICLE_COMMENTS = "articleComments"
    }
}
