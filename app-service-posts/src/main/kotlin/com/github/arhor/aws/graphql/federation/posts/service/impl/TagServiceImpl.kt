package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
import com.github.arhor.aws.graphql.federation.posts.service.TagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TagServiceImpl @Autowired constructor(
    private val tagRepository: TagRepository,
) : TagService {

    override fun getTagsByPostIds(postIds: Set<Long>): Map<Long, List<String>> = when {
        postIds.isNotEmpty() -> {
            tagRepository.findAllByPostIdIn(postIds)
        }

        else -> {
            emptyMap()
        }
    }
}
