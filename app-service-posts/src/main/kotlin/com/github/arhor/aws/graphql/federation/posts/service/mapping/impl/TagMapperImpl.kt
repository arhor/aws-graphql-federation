package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
import com.github.arhor.aws.graphql.federation.posts.service.mapping.TagMapper
import org.springframework.stereotype.Component

@Component
class TagMapperImpl : TagMapper {

    override fun mapToRef(tag: TagEntity): TagRef =
        TagRef.from(tag)

    override fun mapToRefs(tags: Collection<TagEntity>): Set<TagRef> =
        tags.map(::mapToRef).toSet()
}
