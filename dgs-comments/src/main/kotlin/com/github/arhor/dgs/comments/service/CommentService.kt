package com.github.arhor.dgs.comments.service

import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentRequest
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentRequest

interface CommentService {
    fun getCommentsByUserId(userId: Long): Collection<Comment>
    fun getCommentsByUserIds(userIds: Collection<Long>): Map<Long, List<Comment>>
    fun getCommentsByPostIds(postIds: Collection<Long>): Map<Long, List<Comment>>
    fun createComment(request: CreateCommentRequest): Comment
    fun updateComment(request: UpdateCommentRequest): Comment
    fun deleteComment(id: Long)
}
