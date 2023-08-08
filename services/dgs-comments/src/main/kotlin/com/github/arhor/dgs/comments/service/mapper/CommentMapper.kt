package com.github.arhor.dgs.comments.service.mapper

import com.github.arhor.dgs.comments.data.entity.CommentEntity
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentInput

interface CommentMapper {

    fun mapToEntity(input: CreateCommentInput): CommentEntity
    fun mapToDTO(entity: CommentEntity): Comment
}
