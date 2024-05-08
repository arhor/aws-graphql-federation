package com.github.arhor.aws.graphql.federation.posts.service

import java.util.UUID

interface TagService {
    fun getTagsByPostIds(postIds: Set<UUID>): Map<UUID, List<String>>
}
