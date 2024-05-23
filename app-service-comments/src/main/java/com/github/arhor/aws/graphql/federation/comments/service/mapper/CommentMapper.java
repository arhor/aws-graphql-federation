package com.github.arhor.aws.graphql.federation.comments.service.mapper;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;

/**
 * Mapper interface for converting between comment entities and data transfer objects (DTOs).
 */
public interface CommentMapper {

    /**
     * Maps a CreateCommentInput object to a CommentEntity.
     *
     * @param input the input object containing the necessary data to create a comment entity
     * @return the mapped comment entity
     */
    CommentEntity mapToEntity(CreateCommentInput input);

    /**
     * Maps a CommentEntity to a Comment DTO.
     *
     * @param entity the comment entity to map
     * @return the mapped comment DTO
     */
    Comment mapToDto(CommentEntity entity);
}
