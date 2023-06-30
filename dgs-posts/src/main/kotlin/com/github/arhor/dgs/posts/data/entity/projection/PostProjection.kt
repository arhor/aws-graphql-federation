package com.github.arhor.dgs.posts.data.entity.projection

data class PostProjection(
    val id: Long,
    val userId: Long?,
    val header: String,
    val banner: String?,
    val content: String,
)
