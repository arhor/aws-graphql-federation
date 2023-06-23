package com.github.arhor.dgs.articles.api.graphql.dataloader

import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.service.ArticleService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@DgsDataLoader(maxBatchSize = 50)
class ArticleBatchLoader(
    private val asyncExecutor: Executor,
    private val articleService: ArticleService,
) : MappedBatchLoader<Long, List<Article>> {

    override fun load(keys: Set<Long>): CompletableFuture<Map<Long, List<Article>>> {
        return CompletableFuture.supplyAsync({ articleService.getArticlesByUserIds(keys) }, asyncExecutor)
    }
}
