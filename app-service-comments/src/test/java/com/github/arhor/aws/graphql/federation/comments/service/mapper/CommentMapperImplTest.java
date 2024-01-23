package com.github.arhor.aws.graphql.federation.comments.service.mapper;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.from;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@SpringJUnitConfig
@SuppressWarnings("ConstantValue")
class CommentMapperImplTest {

    @Configuration
    @ComponentScan(
        includeFilters = {@Filter(type = ASSIGNABLE_TYPE, classes = CommentMapper.class)},
        useDefaultFilters = false
    )
    static class Config {
    }

    @Autowired
    private CommentMapper commentMapper;

    @Nested
    @DisplayName("method mapToEntity")
    class MapToEntityTest {
        @Test
        void should_correctly_map_create_comment_input_dto_to_entity() {
            // given
            final var input =
                CreateCommentInput.newBuilder()
                    .userId(1L)
                    .postId(2L)
                    .content("user-1 / post-2 / test-comment")
                    .build();

            // when
            final var entity = commentMapper.mapToEntity(input);

            // then
            assertThat(entity)
                .isNotNull()
                .returns(input.getUserId(), from(CommentEntity::userId))
                .returns(input.getPostId(), from(CommentEntity::postId))
                .returns(input.getContent(), from(CommentEntity::content))
                .returns(null, from(CommentEntity::id))
                .returns(null, from(CommentEntity::version))
                .returns(null, from(CommentEntity::createdDateTime))
                .returns(null, from(CommentEntity::updatedDateTime));
        }

        @Test
        void should_return_null_when_incoming_dto_is_null() {
            // given
            final var input = (CreateCommentInput) null;

            // when
            final var entity = commentMapper.mapToEntity(input);

            // then
            assertThat(entity)
                .isNull();
        }
    }

    @Nested
    @DisplayName("method mapToDto")
    class MapToDtoTest {
        @Test
        void should_correctly_map_comment_entity_to_dto() {
            // given
            final var entity =
                CommentEntity.builder()
                    .id(0L)
                    .userId(1L)
                    .postId(2L)
                    .content("user-1 / post-2 / test-comment")
                    .build();

            // when
            final var dto = commentMapper.mapToDto(entity);

            // then
            assertThat(dto)
                .isNotNull()
                .returns(entity.id(), from(Comment::getId))
                .returns(entity.userId(), from(Comment::getUserId))
                .returns(entity.postId(), from(Comment::getPostId))
                .returns(entity.content(), from(Comment::getContent));
        }

        @Test
        void should_return_null_when_incoming_entity_is_null() {
            // given
            final var input = (CommentEntity) null;

            // when
            final var entity = commentMapper.mapToDto(input);

            // then
            assertThat(entity)
                .isNull();
        }

        @Test
        void should_throw_exception_trying_to_map_entity_without_id() {
            // given
            final var entity =
                CommentEntity.builder()
                    .id(null)
                    .build();

            // when
            final var exception = catchException(() -> commentMapper.mapToDto(entity));

            // then
            assertThat(exception)
                .isNotNull()
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Entity must be persisted with assigned id!");
        }
    }
}
