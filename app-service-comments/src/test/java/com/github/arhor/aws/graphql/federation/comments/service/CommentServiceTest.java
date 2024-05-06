package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.CommentRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@SpringJUnitConfig
class CommentServiceTest {

    @Configuration
    @ComponentScan(
        includeFilters = {@Filter(type = ASSIGNABLE_TYPE, classes = CommentService.class)},
        useDefaultFilters = false
    )
    static class Config {
    }

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @Nested
    @DisplayName("CommentService :: getCommentsByUserIds")
    class GetCommentsByUserIdsMethodTest {
        @Test
        void should_return_comments_grouped_by_user_id() {
            // given
            final var userId = 1L;
            final var userIds = List.of(userId);

            final var commentEntities = List.of(
                CommentEntity.builder().id(1L).userId(userId).build(),
                CommentEntity.builder().id(2L).userId(userId).build()
            );
            final var commentDtos = List.of(
                Comment.newBuilder().id(1L).userId(userId).build(),
                Comment.newBuilder().id(2L).userId(userId).build()
            );

            given(commentRepository.findAllByUserIdIn(any()))
                .willAnswer((call) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((call) -> commentDtos.get(0))
                .willAnswer((call) -> commentDtos.get(1));

            // When
            var result = commentService.getCommentsByUserIds(userIds);

            // then
            then(commentRepository)
                .should()
                .findAllByUserIdIn(userIds);

            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(0));
            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(1));

            assertThat(result)
                .isNotNull()
                .containsOnlyKeys(userId)
                .hasEntrySatisfying(userId, (comments) -> {
                    assertThat(comments)
                        .isNotNull()
                        .containsExactlyElementsOf(commentDtos);
                });
        }

        @Test
        void should_not_interact_with_repository_if_userIds_is_empty() {
            // given
            var userIds = Collections.<Long>emptyList();

            // When
            var result = commentService.getCommentsByUserIds(userIds);

            // then
            verifyNoInteractions(commentRepository, commentMapper);

            assertThat(result)
                .isNotNull()
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("CommentService :: getCommentsByPostIds")
    class GetCommentsByPostIdsMethodTest {
        @Test
        void should_return_comments_grouped_by_post_id() {
            // given
            final var postId = 1L;
            final var postIds = List.of(postId);

            final var commentEntities = List.of(
                CommentEntity.builder().id(1L).postId(postId).build(),
                CommentEntity.builder().id(2L).postId(postId).build()
            );
            final var commentDtos = List.of(
                Comment.newBuilder().id(1L).postId(postId).build(),
                Comment.newBuilder().id(2L).postId(postId).build()
            );

            given(commentRepository.findAllByPostIdIn(any()))
                .willAnswer((call) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((call) -> commentDtos.get(0))
                .willAnswer((call) -> commentDtos.get(1));

            // When
            var result = commentService.getCommentsByPostIds(postIds);

            // then
            then(commentRepository)
                .should()
                .findAllByPostIdIn(postIds);

            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(0));
            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(1));

            assertThat(result)
                .isNotNull()
                .containsOnlyKeys(postId)
                .hasEntrySatisfying(postId, (comments) -> {
                    assertThat(comments)
                        .isNotNull()
                        .containsExactlyElementsOf(commentDtos);
                });
        }

        @Test
        void should_not_interact_with_repository_if_postIds_is_empty() {
            // given
            var postIds = Collections.<Long>emptyList();

            // When
            var result = commentService.getCommentsByPostIds(postIds);

            // then
            then(commentRepository)
                .shouldHaveNoInteractions();
            then(commentMapper)
                .shouldHaveNoInteractions();

            assertThat(result)
                .isNotNull()
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("CommentService :: createComment")
    class CreateCommentMethodTest {
        @Test
        void should_create_comment_and_return_it_in_the_result() {
            // Given
            final var input = CreateCommentInput.newBuilder().build();
            final var entity = CommentEntity.builder().build();
            final var dto = Comment.newBuilder().build();

            given(commentMapper.mapToEntity(any()))
                .willReturn(entity);
            given(commentRepository.save(any()))
                .willReturn(entity);
            given(commentMapper.mapToDto(any()))
                .willReturn(dto);

            // When
            final var createCommentResult = commentService.createComment(input);

            // Then
            then(commentMapper)
                .should()
                .mapToEntity(input);
            then(commentRepository)
                .should()
                .save(entity);
            then(commentMapper)
                .should()
                .mapToDto(entity);

            assertThat(createCommentResult)
                .isNotNull()
                .extracting(CreateCommentResult::getComment)
                .isNotNull()
                .isEqualTo(dto);
        }
    }
}
