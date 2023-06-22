package com.github.arhor.dgs.articles.api.datafetcher

import com.github.arhor.dgs.articles.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.ArticlesLookupInput
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.UpdateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.User
import com.github.arhor.dgs.articles.service.ArticleService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class ArticleFetcher(private val articleService: ArticleService) {

    @DgsMutation
    fun createArticle(@InputArgument input: CreateArticleInput): Article =
        articleService.createArticle(input)

    @DgsMutation
    fun updateArticle(@InputArgument input: UpdateArticleInput): Article =
        articleService.updateArticle(input)

    @DgsMutation
    fun deleteArticle(@InputArgument id: Long): Boolean =
        articleService.deleteArticle(id)

    @DgsQuery
    fun article(@InputArgument id: Long): Article =
        articleService.getArticleById(id)

    @DgsQuery
    fun articles(@InputArgument input: ArticlesLookupInput): List<Article> =
        articleService.getArticles(input)

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun user(values: Map<String, Any>): User =
        User(id = values[USER.Id] as Long)
}
