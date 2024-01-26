package com.github.arhor.aws.graphql.federation.posts.service.mapping

import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef

interface TagMapper {
    fun mapToRef(tag: TagEntity): TagRef
    fun mapToRefs(tags: Collection<TagEntity>): Set<TagRef>
}
