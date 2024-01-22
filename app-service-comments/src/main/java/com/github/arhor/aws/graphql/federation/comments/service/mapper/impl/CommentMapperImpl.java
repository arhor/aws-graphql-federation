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
        return new CommentEntity(
            input.getUserId(),
            input.getPostId(),
            input.getContent()
        );
    }

    @Override
    public Comment mapToDTO(final CommentEntity entity) {
        return new Comment(
            Objects.requireNonNull(entity.id(), "Entity must be persisted with assigned id!"),
            entity.userId(),
            entity.postId(),
            entity.content()
        );
    }
}
