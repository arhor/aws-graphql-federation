package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.dgs.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.dgs.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.mapping.OptionsMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostMapperImpl @Autowired constructor(
    private val optionsMapper: OptionsMapper,
) : PostMapper {

    override fun map(input: CreatePostInput, banner: String?, tags: Set<TagRef>): PostEntity {
        return PostEntity(
            userId = input.userId,
            header = input.header,
            banner = banner,
            content = input.content,
            options = optionsMapper.map(input.options),
            tags = tags
        )
    }

    override fun map(entity: PostEntity): Post {
        return Post(
            id = entity.id!!,
            userId = entity.userId,
            header = entity.header,
            banner = entity.banner,
            content = entity.content,
            options = entity.options.items.toList(),
        )
    }

    override fun map(projection: PostProjection): Post {
        return Post(
            id = projection.id,
            userId = projection.userId,
            header = projection.header,
            banner = projection.banner,
            content = projection.content,
            options = optionsMapper.map(projection.options),
        )
    }
}
