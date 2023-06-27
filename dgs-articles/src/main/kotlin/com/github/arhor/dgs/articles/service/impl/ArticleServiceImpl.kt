package com.github.arhor.dgs.articles.service.impl

import com.github.arhor.dgs.articles.data.entity.TagEntity
import com.github.arhor.dgs.articles.data.entity.TagRef
import com.github.arhor.dgs.articles.data.repository.ArticleRepository
import com.github.arhor.dgs.articles.data.repository.FileRepository
import com.github.arhor.dgs.articles.data.repository.TagRepository
import com.github.arhor.dgs.articles.generated.graphql.DgsConstants.ARTICLE
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.ArticlesLookupInput
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.UpdateArticleInput
import com.github.arhor.dgs.articles.service.ArticleMapper
import com.github.arhor.dgs.articles.service.ArticleService
import com.github.arhor.dgs.lib.exception.EntityNotFoundException
import com.github.arhor.dgs.lib.exception.Operation
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ArticleServiceImpl(
    private val articleMapper: ArticleMapper,
    private val articleRepository: ArticleRepository,
    private val fileRepository: FileRepository,
    private val tagRepository: TagRepository,
) : ArticleService {

    @Transactional
    override fun createArticle(input: CreateArticleInput): Article {
        val tagRefs = materialize(input.tags)
        val bannerFilename = input.banner?.let { "${input.userId}__${UUID.randomUUID()}__${it.name}" }

        val article = articleMapper.mapToEntity(dto = input, banner = bannerFilename, tags = tagRefs)
            .let(articleRepository::save)
            .let(articleMapper::mapToDTO)

        if (bannerFilename != null) {
            input.banner.inputStream.use {
                fileRepository.upload(filename = bannerFilename, data = it)
            }
        }
        return article
    }

    @Transactional
    @Retryable(retryFor = [OptimisticLockingFailureException::class])
    override fun updateArticle(input: UpdateArticleInput): Article {
        val initialArticle = articleRepository.findByIdOrNull(input.id) ?: throw EntityNotFoundException(
            entity = ARTICLE.TYPE_NAME,
            condition = "${ARTICLE.Id} = ${input.id}",
            operation = Operation.UPDATE,
        )
        var currentArticle = initialArticle

        input.header?.let { currentArticle = currentArticle.copy(header = it) }
        input.content?.let { currentArticle = currentArticle.copy(content = it) }
        input.tags?.let { currentArticle = currentArticle.copy(tags = materialize(it)) }

        return articleMapper.mapToDTO(
            entity = when (currentArticle != initialArticle) {
                true -> articleRepository.save(currentArticle)
                else -> currentArticle
            }
        )
    }

    @Transactional
    override fun deleteArticle(id: Long): Boolean {
        val article = articleRepository.findByIdOrNull(id) ?: throw EntityNotFoundException(
            entity = ARTICLE.TYPE_NAME,
            condition = "${ARTICLE.Id} = $id",
            operation = Operation.DELETE,
        )
        articleRepository.delete(article)
        article.banner?.let { fileRepository.delete(it) }
        return true
    }

    @Transactional(readOnly = true)
    override fun getArticleById(id: Long): Article {
        return articleRepository.findByIdOrNull(id)?.let(articleMapper::mapToDTO)
            ?: throw EntityNotFoundException(
                entity = ARTICLE.TYPE_NAME,
                condition = "${ARTICLE.Id} = $id",
                operation = Operation.READ,
            )
    }

    @Transactional(readOnly = true)
    override fun getArticles(input: ArticlesLookupInput): List<Article> {
        return articleRepository
            .findAll(limit = input.size, offset = input.page * input.size)
            .map(articleMapper::mapToDTO)
            .toList()
    }

    @Transactional(readOnly = true)
    override fun getArticlesByUserIds(userIds: Set<Long>): Map<Long, List<Article>> = when {
        userIds.isNotEmpty() -> {
            articleRepository
                .findAllByUserIdIn(userIds)
                .groupBy({ it.userId!! }, articleMapper::mapToDTO)
        }

        else -> emptyMap()
    }

    /**
     * Persists missing tags to the database, returning tag references.
     */
    private fun materialize(tags: List<String>?): Set<TagRef> = when {
        tags != null -> {
            val presentTags = tagRepository.findAllByNameIn(tags)
            val missingTags = (tags - presentTags.mapTo(HashSet()) { it.name }).map(TagEntity::create)
            val createdTags = tagRepository.saveAll(missingTags)

            val initialCapacity = presentTags.size + createdTags.size

            sequenceOf(presentTags, createdTags)
                .flatten()
                .mapTo(HashSet(initialCapacity), TagRef::create)
        }

        else -> emptySet()
    }
}
