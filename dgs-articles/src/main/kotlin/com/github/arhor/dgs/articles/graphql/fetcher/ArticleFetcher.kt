package com.github.arhor.dgs.articles.graphql.fetcher

import com.github.arhor.dgs.articles.generated.graphql.DgsConstants
import com.github.arhor.dgs.articles.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleRequest
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
    fun createTopic(@InputArgument request: CreateArticleRequest) =
        articleService.createArticle(request)

    @DgsQuery
    fun topics(@InputArgument limit: Int, @InputArgument offset: Int) =
        articleService.getArticles(limit, offset)

    @DgsEntityFetcher(name = DgsConstants.USER.TYPE_NAME)
    fun fetchUser(values: Map<String, Any>) =
        User(id = values["id"]!!.toString())
}
