package com.github.arhor.aws.graphql.federation.comments.service.mapper;

import com.github.arhor.aws.graphql.federation.comments.data.model.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Mapper interface for converting between comment entities and data transfer objects (DTOs).
 */
public interface CommentMapper {

    /**
     * Maps a CreateCommentInput object to a CommentEntity.
     *
     * @param input  the input object containing the necessary data to create a comment entity
     * @param userId the ID of a user creating comment
     * @return the mapped comment entity
     */
    @NotNull
    CommentEntity mapToEntity(@NotNull CreateCommentInput input, @NotNull UUID userId);

    /**
     * Maps a CommentEntity to a Comment DTO.
     *
     * @param entity the comment entity to map
     * @return the mapped comment DTO
     */
    @NotNull
    Comment mapToDto(@NotNull CommentEntity entity);
}
