package com.github.arhor.dgs.articles.service.impl

import com.github.arhor.dgs.articles.data.entity.TagEntity
import com.github.arhor.dgs.articles.data.repository.ArticleRepository
import com.github.arhor.dgs.articles.data.repository.TagRepository
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.ArticlesLookupInput
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.UpdateArticleInput
import com.github.arhor.dgs.articles.service.ArticleMapper
import com.github.arhor.dgs.articles.service.ArticleService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleServiceImpl(
    private val articleMapper: ArticleMapper,
    private val articleRepository: ArticleRepository,
    private val tagRepository: TagRepository,
) : ArticleService {

    @Transactional
    override fun createArticle(input: CreateArticleInput): Article {
        return articleMapper.mapToEntity(input)
            .withTags(input.tags.materialize())
            .let(articleRepository::save)
            .let(articleMapper::mapToDTO)
    }

    @Transactional
    override fun updateArticle(input: UpdateArticleInput): Article {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun deleteArticle(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    @Transactional(readOnly = true)
    override fun getArticleById(id: Long): Article {
        TODO("Not yet implemented")
    }

    @Transactional(readOnly = true)
    override fun getArticles(input: ArticlesLookupInput): List<Article> {
        return articleRepository
            .findAll(limit = input.size, offset = input.page * input.size)
            .map(articleMapper::mapToDTO)
            .toList()
    }

    @Transactional(readOnly = true)
    override fun getArticlesByUserIds(userIds: Set<Long>): Map<Long, List<Article>> =
        when {
            userIds.isNotEmpty() -> {
                articleRepository
                    .findAllByUserIdIn(userIds)
                    .groupBy({ it.userId!! }, articleMapper::mapToDTO)
            }

            else -> {
                emptyMap()
            }
        }

    /**
     * Persists missing tags to the database, returning all tag entities with id property set.
     */
    private fun List<String>?.materialize(): List<TagEntity> {
        return if (this != null) {
            val presentTags = tagRepository.findAllByNameIn(this)
            val missingTags = (this - presentTags.mapTo(HashSet()) { it.name }).map(TagEntity::new)
            val createdTags = tagRepository.saveAll(missingTags)

            presentTags + createdTags
        } else {
            emptyList()
        }
    }
}
