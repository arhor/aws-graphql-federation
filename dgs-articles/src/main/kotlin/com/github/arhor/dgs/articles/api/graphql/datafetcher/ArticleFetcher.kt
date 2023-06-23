package com.github.arhor.dgs.articles.api.graphql.datafetcher

import com.github.arhor.dgs.articles.api.graphql.dataloader.ArticleBatchLoader
import com.github.arhor.dgs.articles.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.ArticlesLookupInput
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.UpdateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.User
import com.github.arhor.dgs.articles.service.ArticleService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import java.util.concurrent.CompletableFuture

@DgsComponent
class ArticleFetcher(
    private val articleService: ArticleService,
) {
    /* Queries */

    @DgsQuery
    fun article(@InputArgument id: Long): Article =
        articleService.getArticleById(id)

    @DgsQuery
    fun articles(@InputArgument input: ArticlesLookupInput): List<Article> =
        articleService.getArticles(input)

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Articles)
    fun userArticles(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Article>> {
        val loader = dfe.getDataLoader<Long, List<Article>>(ArticleBatchLoader::class.java)
        val source = dfe.getSource<User>()

        return loader.load(source.id)
    }

    /* Mutations */

    @DgsMutation
    fun createArticle(@InputArgument input: CreateArticleInput): Article =
        articleService.createArticle(input)

    @DgsMutation
    fun updateArticle(@InputArgument input: UpdateArticleInput): Article =
        articleService.updateArticle(input)

    @DgsMutation
    fun deleteArticle(@InputArgument id: Long): Boolean =
        articleService.deleteArticle(id)
}
