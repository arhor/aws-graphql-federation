package com.github.arhor.dgs.articles.service.impl

import com.github.arhor.dgs.articles.data.repository.ArticleRepository
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleRequest
import com.github.arhor.dgs.articles.generated.graphql.types.UpdateArticleInput
import com.github.arhor.dgs.articles.service.ArticleService
import com.github.arhor.dgs.articles.service.mapper.ArticleMapper
import com.github.arhor.dgs.lib.OffsetBasedPageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val articleMapper: ArticleMapper,
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
    override fun getArticles(limit: Int, offset: Int): List<Article> {
        return articleRepository
            .findAll(OffsetBasedPageRequest(offset, limit))
            .map(articleMapper::mapToDTO)
            .toList()
    }
}
