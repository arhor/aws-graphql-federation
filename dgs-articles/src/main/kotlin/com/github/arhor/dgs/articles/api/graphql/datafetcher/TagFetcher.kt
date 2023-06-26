package com.github.arhor.dgs.articles.api.graphql.datafetcher

import com.github.arhor.dgs.articles.api.graphql.dataloader.TagBatchLoader
import com.github.arhor.dgs.articles.generated.graphql.DgsConstants
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.service.TagService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import java.util.concurrent.CompletableFuture

@DgsComponent
class TagFetcher(
    private val tagService: TagService,
) {
    /* Queries */

    @DgsData(parentType = DgsConstants.ARTICLE.TYPE_NAME)
    fun tags(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<String>> {
        val loader = dfe.getDataLoader<Long, List<String>>(TagBatchLoader::class.java)
        val source = dfe.getSource<Article>()

        return loader.load(source.id)
    }
}
