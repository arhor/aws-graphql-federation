package com.github.arhor.dgs.articles.service

import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.ArticlesLookupInput
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.UpdateArticleInput

interface ArticleService {
    fun createArticle(input: CreateArticleInput): Article
    fun updateArticle(input: UpdateArticleInput): Article
    fun deleteArticle(id: Long): Boolean
    fun getArticleById(id: Long): Article
    fun getArticles(input: ArticlesLookupInput): List<Article>
}
