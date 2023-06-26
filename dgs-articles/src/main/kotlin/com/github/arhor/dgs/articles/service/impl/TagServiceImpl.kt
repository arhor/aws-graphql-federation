package com.github.arhor.dgs.articles.service.impl

import com.github.arhor.dgs.articles.data.repository.TagRepository
import com.github.arhor.dgs.articles.service.TagService
import org.springframework.stereotype.Service

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository,
) : TagService {

    override fun getTagsByArticleIds(articleIds: Set<Long>): Map<Long, List<String>> =
        when {
            articleIds.isNotEmpty() -> {
                tagRepository
                    .findAllByArticleIdIn(articleIds)
                    .groupBy({ it.articleId }, { it.name })
            }

            else -> {
                emptyMap()
            }
        }
}
