package com.github.arhor.aws.graphql.federation.comments.service.mapper.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class CommentMapperImpl implements CommentMapper {

    @Nonnull
    @Override
    public CommentEntity mapToEntity(@Nonnull final CreateCommentInput input, @Nonnull final UUID userId) {
        return CommentEntity.builder()
            .userId(userId)
            .postId(input.getPostId())
            .prntId(input.getPrntId())
            .content(input.getContent())
            .build();
    }

    @Nonnull
    @Override
    public Comment mapToDto(@Nonnull final CommentEntity entity) {
        return Comment.newBuilder()
            .id(Objects.requireNonNull(entity.id(), "Entity must be persisted with assigned id!"))
            .userId(entity.userId())
            .postId(entity.postId())
            .prntId(entity.prntId())
            .content(entity.content())
            .build();
    }
}
