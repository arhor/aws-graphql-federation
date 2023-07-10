package com.github.arhor.dgs.comments.service

import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentInput
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentInput

interface CommentService {
    fun getCommentsByUserIds(userIds: Collection<Long>): Map<Long, List<Comment>>
    fun getCommentsByPostIds(postIds: Collection<Long>): Map<Long, List<Comment>>
    fun createComment(input: CreateCommentInput): Comment
    fun updateComment(input: UpdateCommentInput): Comment
    fun deleteComment(id: Long): Boolean
    fun deleteCommentsFromPost(postId: Long)
    fun unlinkCommentsFromUser(userId: Long)
}
