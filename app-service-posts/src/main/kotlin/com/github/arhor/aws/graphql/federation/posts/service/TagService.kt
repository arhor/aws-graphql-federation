package com.github.arhor.aws.graphql.federation.posts.service

interface TagService {
    fun getTagsByPostIds(postIds: Set<Long>): Map<Long, List<String>>
}
