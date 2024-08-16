package com.github.arhor.aws.graphql.federation.comments.service.mapper;

import com.github.arhor.aws.graphql.federation.comments.data.model.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.from;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@SpringJUnitConfig
class CommentMapperImplTest {

    @Configuration
    @ComponentScan(
        includeFilters = {@Filter(type = ASSIGNABLE_TYPE, classes = CommentMapper.class)},
        useDefaultFilters = false
    )
    static class Config {}

    @Autowired
    private CommentMapper commentMapper;

    @Nested
    @DisplayName("CommentMapper :: mapToEntity")
    class MapToEntityTest {
        @Test
        void should_correctly_map_create_comment_input_dto_to_entity() {
            // Given
            final var input =
                CreateCommentInput.newBuilder()
                    .postId(ConstantsKt.getOMNI_UUID_VAL())
                    .content("user-1 / post-2 / test-comment")
                    .build();
            final var userId = ConstantsKt.getZERO_UUID_VAL();

            // When
            final var entity = commentMapper.mapToEntity(input, userId);

            // Then
            assertThat(entity)
                .isNotNull()
                .returns(userId, from(CommentEntity::userId))
                .returns(input.getPostId(), from(CommentEntity::postId))
                .returns(input.getContent(), from(CommentEntity::content))
                .returns(null, from(CommentEntity::id))
                .returns(null, from(CommentEntity::version))
                .returns(null, from(CommentEntity::createdDateTime))
                .returns(null, from(CommentEntity::updatedDateTime));
        }
    }

    @Nested
    @DisplayName("CommentMapper :: mapToDto")
    class MapToDtoTest {
        @Test
        void should_correctly_map_comment_entity_to_dto() {
            // Given
            final var entity =
                CommentEntity.builder()
                    .id(ConstantsKt.getTEST_1_UUID_VAL())
                    .userId(ConstantsKt.getZERO_UUID_VAL())
                    .postId(ConstantsKt.getOMNI_UUID_VAL())
                    .content("user-1 / post-2 / test-comment")
                    .build();

            // When
            final var dto = commentMapper.mapToDto(entity);

            // Then
            assertThat(dto)
                .isNotNull()
                .returns(entity.id(), from(Comment::getId))
                .returns(entity.userId(), from(Comment::getUserId))
                .returns(entity.postId(), from(Comment::getPostId))
                .returns(entity.content(), from(Comment::getContent));
        }

        @Test
        void should_throw_exception_trying_to_map_entity_without_id() {
            // Given
            final var entity =
                CommentEntity.builder()
                    .id(null)
                    .build();

            // When
            final var exception = catchThrowable(() -> commentMapper.mapToDto(entity));

            // Then
            assertThat(exception)
                .isNotNull()
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Entity must be persisted with assigned id!");
        }
    }
}
