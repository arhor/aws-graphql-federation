package com.github.arhor.dgs.articles.service

import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleRequest

interface ArticleService {
    fun createArticle(request: CreateArticleRequest): Article
    fun getArticles(limit: Int, offset: Int): List<Article>
}
