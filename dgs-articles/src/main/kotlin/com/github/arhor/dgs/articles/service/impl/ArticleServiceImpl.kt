package com.github.arhor.dgs.articles.service.impl

import com.github.arhor.dgs.articles.data.entity.TagEntity
import com.github.arhor.dgs.articles.data.entity.TagRef
import com.github.arhor.dgs.articles.data.repository.ArticleRepository
import com.github.arhor.dgs.articles.data.repository.FileRepository
import com.github.arhor.dgs.articles.data.repository.TagRepository
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.ArticlesLookupInput
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.articles.generated.graphql.types.UpdateArticleInput
import com.github.arhor.dgs.articles.service.ArticleMapper
import com.github.arhor.dgs.articles.service.ArticleService
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
        val bannerFilename = input.banner?.let { it.name + UUID.randomUUID() }

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
    override fun updateArticle(input: UpdateArticleInput): Article {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun deleteArticle(id: Long): Boolean {
        // delete article
        // ensure there ar e no m2m links
        // delete banner image from S3
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
