package com.github.arhor.aws.graphql.federation.comments.service.mapper.impl

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentInput
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper
import org.springframework.stereotype.Component

@Component
class CommentMapperImpl : CommentMapper {

    override fun mapToEntity(input: CreateCommentInput): CommentEntity {
        return CommentEntity(
            userId = input.userId,
            postId = input.postId,
            content = input.content,
        )
    }

    override fun mapToDTO(entity: CommentEntity): Comment {
        return Comment(
            id = entity.id ?: throw IllegalArgumentException("Entity must be persisted with assigned id!"),
            userId = entity.userId,
            postId = entity.postId,
            content = entity.content,
        )
    }
}
