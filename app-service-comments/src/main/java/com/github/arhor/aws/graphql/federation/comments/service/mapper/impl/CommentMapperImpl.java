package com.github.arhor.aws.graphql.federation.comments.service.mapper.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentEntity mapToEntity(final CreateCommentInput input) {
        return (input == null) ? null : CommentEntity.builder()
            .userId(input.getUserId())
            .postId(input.getPostId())
            .prntId(input.getPrntId())
            .content(input.getContent())
            .build();
    }

    @Override
    public Comment mapToDto(final CommentEntity entity) {
        return (entity == null) ? null : Comment.newBuilder()
            .id(Objects.requireNonNull(entity.id(), "Entity must be persisted with assigned id!"))
            .userId(entity.userId())
            .postId(entity.postId())
            .prntId(entity.prntId())
            .content(entity.content())
            .build();
    }
}
