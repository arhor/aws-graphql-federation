package com.github.arhor.aws.graphql.federation.posts.service.mapping

import com.github.arhor.aws.graphql.federation.posts.data.model.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.model.TagRef
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import org.springframework.data.domain.Page
import java.util.UUID

interface PostMapper {
    fun mapToEntity(input: CreatePostInput, userId: UUID, tags: Set<TagRef>?): PostEntity
    fun mapToPost(entity: PostEntity): Post
    fun mapToPostPageFromEntity(page: Page<PostEntity>): PostPage
}
