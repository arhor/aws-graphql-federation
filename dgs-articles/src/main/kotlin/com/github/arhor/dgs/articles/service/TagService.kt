package com.github.arhor.dgs.articles.service

import com.github.arhor.dgs.articles.generated.graphql.types.CreateTagInput
import com.github.arhor.dgs.articles.generated.graphql.types.Tag

interface TagService {
    fun getTagsByArticleIds(keys: Set<Long>): Map<Long, List<Tag>>
    fun createTag(input: CreateTagInput): Tag
    fun deleteTag(id: Long): Boolean
}
