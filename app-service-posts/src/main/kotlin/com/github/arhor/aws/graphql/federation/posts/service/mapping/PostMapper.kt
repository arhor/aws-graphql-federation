package com.github.arhor.aws.graphql.federation.posts.service.mapping

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post

interface PostMapper {
    fun map(input: CreatePostInput, tags: Set<TagEntity>): PostEntity
    fun map(entity: PostEntity): Post
    fun map(projection: PostProjection): Post
}
