package com.github.arhor.dgs.posts.data.entity.projection

import com.github.arhor.dgs.posts.data.entity.PostEntity

data class PostProjection(
    val id: Long,
    val userId: Long?,
    val header: String,
    val banner: String?,
    val content: String,
    val options: PostEntity.Options,
)
