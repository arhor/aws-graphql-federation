package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import com.github.arhor.aws.graphql.federation.posts.service.mapping.OptionsMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.TagMapper
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class PostMapperImpl(
    private val optionsMapper: OptionsMapper,
    private val tagMapper: TagMapper,
) : PostMapper {

    override fun mapToEntity(input: CreatePostInput, tags: Set<TagEntity>): PostEntity {
        return PostEntity(
            userId = input.userId,
            title = input.title,
            content = input.content,
            options = optionsMapper.mapFromList(input.options),
            tags = tagMapper.mapToRefs(tags)
        )
    }

    override fun mapToPost(entity: PostEntity): Post {
        return Post(
            id = entity.id!!,
            userId = entity.userId,
            title = entity.title,
            content = entity.content,
            options = optionsMapper.mapIntoList(entity.options),
        )
    }

    override fun mapToPost(projection: PostProjection): Post {
        return Post(
            id = projection.id,
            userId = projection.userId,
            title = projection.title,
            content = projection.content,
            options = optionsMapper.mapIntoList(projection.options),
        )
    }

    override fun mapToPostPage(page: Page<PostEntity>): PostPage {
        return PostPage(
            data = page.content.filterNotNull().map(::mapToPost),
            page = page.number,
            size = page.size,
            hasPrev = page.hasPrevious(),
            hasNext = page.hasNext(),
        )
    }

    override fun mapToPostPage(page: Page<PostProjection>): PostPage {
        return PostPage(
            data = page.content.filterNotNull().map(::mapToPost),
            page = page.number,
            size = page.size,
            hasPrev = page.hasPrevious(),
            hasNext = page.hasNext(),
        )
    }
}
