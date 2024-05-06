package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.mapping.OptionsMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.TagMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostMapperImpl @Autowired constructor(
    private val optionsMapper: OptionsMapper,
    private val tagMapper: TagMapper,
) : PostMapper {

    override fun map(input: CreatePostInput, tags: Set<TagEntity>): PostEntity {
        return PostEntity(
            userId = input.userId,
            header = input.header,
            content = input.content,
            options = optionsMapper.mapFromList(input.options),
            tags = tagMapper.mapToRefs(tags)
        )
    }

    override fun map(entity: PostEntity): Post {
        return Post(
            id = entity.id!!,
            userId = entity.userId,
            header = entity.header,
            content = entity.content,
            options = entity.options.items.toList(),
        )
    }

    override fun map(projection: PostProjection): Post {
        return Post(
            id = projection.id,
            userId = projection.userId,
            header = projection.header,
            content = projection.content,
            options = optionsMapper.mapIntoList(projection.options),
        )
    }
}
