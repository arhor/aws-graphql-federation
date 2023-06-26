package com.github.arhor.dgs.articles.service

interface TagService {
    fun getTagsByArticleIds(articleIds: Set<Long>): Map<Long, List<String>>
}
