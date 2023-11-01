package com.github.arhor.aws.graphql.federation.posts.service.mapping

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.dgs.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.dgs.posts.generated.graphql.types.Post

interface PostMapper {
    fun map(input: CreatePostInput, banner: String?, tags: Set<TagRef>): PostEntity
    fun map(entity: PostEntity): Post
    fun map(projection: PostProjection): Post
}
