package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
import com.github.arhor.aws.graphql.federation.posts.service.TagService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository,
) : TagService {

    override fun getTagsByPostIds(postIds: Set<UUID>): Map<UUID, List<String>> = when {
        postIds.isNotEmpty() -> {
            tagRepository.findAllByPostIdIn(postIds).toMutableMap().apply {
                for (postId in postIds) {
                    computeIfAbsent(postId) { emptyList() }
                }
            }
        }

        else -> {
            emptyMap()
        }
    }
}
