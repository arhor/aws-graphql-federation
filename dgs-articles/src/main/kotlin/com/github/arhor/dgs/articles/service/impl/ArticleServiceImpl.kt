package com.github.arhor.dgs.articles.service.impl

import com.github.arhor.dgs.articles.data.repository.ArticleRepository
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.ArticlesLookupInput
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.UpdateArticleInput
import com.github.arhor.dgs.articles.service.ArticleMapper
import com.github.arhor.dgs.articles.service.ArticleService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleServiceImpl(
    private val articleMapper: ArticleMapper,
    private val articleRepository: ArticleRepository,
) : ArticleService {

    @Transactional
    override fun createArticle(input: CreateArticleInput): Article {
        return articleMapper.mapToEntity(input)
            .let(articleRepository::save)
            .let(articleMapper::mapToDTO)
    }

    override fun updateArticle(input: UpdateArticleInput): Article {
        TODO("Not yet implemented")
    }

    override fun deleteArticle(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun getArticleById(id: Long): Article {
        TODO("Not yet implemented")
    }

    @Transactional(readOnly = true)
    override fun getArticles(input: ArticlesLookupInput): List<Article> {
        return articleRepository
            .findAll(PageRequest.of(input.page, input.size))
            .map(articleMapper::mapToDTO)
            .toList()
    }

    override fun getArticlesByUserIds(userIds: Set<Long>): Map<Long, List<Article>> {
        TODO("Not yet implemented")
    }
}
