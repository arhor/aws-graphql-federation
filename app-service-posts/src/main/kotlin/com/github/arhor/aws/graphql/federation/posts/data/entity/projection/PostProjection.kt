package com.github.arhor.aws.graphql.federation.posts.data.entity.projection

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity

data class PostProjection(
    val id: Long,
    val userId: Long?,
    val header: String,
    val banner: String?,
    val content: String,
    val options: PostEntity.Options,
)
