package com.github.arhor.dgs.articles.api.graphql.datafetcher

import com.github.arhor.dgs.articles.api.graphql.dataloader.ArticleBatchLoader
import com.github.arhor.dgs.articles.generated.graphql.DgsConstants
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.CreateTagInput
import com.github.arhor.dgs.articles.generated.graphql.types.Tag
import com.github.arhor.dgs.articles.service.TagService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.concurrent.CompletableFuture

@DgsComponent
class TagFetcher(
    private val tagService: TagService,
) {
    /* Queries */

    @DgsData(parentType = DgsConstants.ARTICLE.TYPE_NAME)
    fun tags(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Tag>> {
        val loader = dfe.getDataLoader<Long, List<Tag>>(ArticleBatchLoader::class.java)
        val source = dfe.getSource<Article>()

        return loader.load(source.id)
    }

    /* Mutations */

    @DgsMutation
    fun createTag(@InputArgument input: CreateTagInput): Tag =
        tagService.createTag(input)

    @DgsMutation
    fun deleteTag(@InputArgument id: Long): Boolean =
        tagService.deleteTag(id)
}
