package com.github.arhor.dgs.posts.service

interface TagService {
    fun getTagsByArticleIds(postIds: Set<Long>): Map<Long, List<String>>
}
