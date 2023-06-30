package com.github.arhor.dgs.posts.service.impl

import com.github.arhor.dgs.posts.data.repository.TagRepository
import com.github.arhor.dgs.posts.service.TagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TagServiceImpl @Autowired constructor(
    private val tagRepository: TagRepository,
) : TagService {

    override fun getTagsByArticleIds(postIds: Set<Long>): Map<Long, List<String>> =
        when {
            postIds.isNotEmpty() -> {
                tagRepository.findAllByArticleIdIn(postIds)
            }

            else -> {
                emptyMap()
            }
        }
}
