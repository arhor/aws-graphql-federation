package com.github.arhor.aws.graphql.federation.comments.service

import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentInput
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentResult
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentInput
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentResult

interface CommentService {
    fun getCommentsByUserIds(userIds: Collection<Long>): Map<Long, List<Comment>>
    fun getCommentsByPostIds(postIds: Collection<Long>): Map<Long, List<Comment>>
    fun createComment(input: CreateCommentInput): CreateCommentResult
    fun updateComment(input: UpdateCommentInput): UpdateCommentResult
    fun deleteComment(id: Long): Boolean
    fun unlinkCommentsFromUser(userId: Long)
    fun deleteCommentsFromPost(postId: Long)
}
