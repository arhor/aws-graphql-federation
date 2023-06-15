package com.github.arhor.dgs.comments.service

import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentRequest
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentRequest

interface CommentService {
    fun createComment(request: CreateCommentRequest): Comment
    fun updateComment(request: UpdateCommentRequest): Comment
    fun deleteComment(id: Long)
    fun getCommentsUserId(userId: String): Collection<Comment>
    fun getCommentsByUserIds(userIds: Collection<String>): Map<String, List<Comment>>
    fun getCommentsByTopicId(articleId: String): Collection<Comment>
    fun getCommentsByTopicIds(articleIds: Collection<String>): Map<String, List<Comment>>
}
