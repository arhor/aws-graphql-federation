package com.github.arhor.aws.graphql.federation.comments.service.mapper;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;

public interface CommentMapper {

    CommentEntity mapToEntity(CreateCommentInput input);

    Comment mapToDto(CommentEntity entity);
}
