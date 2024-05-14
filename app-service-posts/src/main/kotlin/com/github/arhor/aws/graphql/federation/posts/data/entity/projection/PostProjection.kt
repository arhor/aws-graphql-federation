package com.github.arhor.aws.graphql.federation.posts.data.entity.projection

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import java.util.UUID

data class PostProjection(
    val id: UUID,
    val userId: UUID?,
    val title: String,
    val content: String,
    val options: PostEntity.Options,
)
