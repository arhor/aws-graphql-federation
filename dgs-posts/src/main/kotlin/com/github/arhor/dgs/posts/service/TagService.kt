package com.github.arhor.dgs.posts.service

interface TagService {
    fun getTagsByPostIds(postIds: Set<Long>): Map<Long, List<String>>
}
