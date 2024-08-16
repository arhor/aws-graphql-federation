package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.model.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.model.TagRef
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.mapping.PostMapper
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PostMapperImpl : PostMapper {

    override fun mapToEntity(input: CreatePostInput, userId: UUID, tags: Set<TagRef>?): PostEntity {
        return PostEntity(
            userId = userId,
            title = input.title,
            content = input.content,
            tags = tags ?: emptySet(),
        )
    }

    override fun mapToPost(entity: PostEntity): Post {
        return Post(
            id = entity.id!!,
            userId = entity.userId,
            title = entity.title,
            content = entity.content,
            likedBy = entity.likes.map { User(id = it.userId) }
        )
    }

    override fun mapToPostPageFromEntity(page: Page<PostEntity>): PostPage {
        return PostPage(
            data = page.content.filterNotNull().map(::mapToPost),
            page = page.number,
            size = page.size,
            hasPrev = page.hasPrevious(),
            hasNext = page.hasNext(),
        )
    }
}
