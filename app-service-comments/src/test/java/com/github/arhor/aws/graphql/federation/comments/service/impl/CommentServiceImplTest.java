package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.CommentRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CommentServiceImplTest {

    private final CommentRepository commentRepository = mock();
    private final CommentMapper commentMapper = mock();
    private final PostRepresentationRepository postRepository = mock();
    private final UserRepresentationRepository userRepository = mock();

    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(
            commentRepository,
            commentMapper,
            postRepository,
            userRepository
        );
        commentService.initialize();
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
            commentRepository,
            commentMapper,
            postRepository,
            userRepository
        );
    }

    @Nested
    @DisplayName("CommentService :: getCommentsByUserIds")
    class GetCommentsByUserIdsMethodTest {
        @Test
        void should_return_comments_grouped_by_user_id() {
            // Given
            final var userId = UUID.randomUUID();
            final var userIds = List.of(userId);

            final var commentEntities = List.of(
                CommentEntity.builder().id(UUID.randomUUID()).userId(userId).build(),
                CommentEntity.builder().id(UUID.randomUUID()).userId(userId).build()
            );
            final var commentDtos = commentEntities.stream()
                .map(it -> Comment.newBuilder().id(it.id()).userId(it.userId()).build())
                .toList();

            given(commentRepository.findAllByUserIdIn(any()))
                .willAnswer((call) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((call) -> commentDtos.get(0))
                .willAnswer((call) -> commentDtos.get(1));

            // When
            var result = commentService.getCommentsByUserIds(userIds);

            // Then
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
            // Given
            var userIds = Collections.<UUID>emptyList();

            // When
            var result = commentService.getCommentsByUserIds(userIds);

            // Then
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
            // Given
            final var postId = UUID.randomUUID();
            final var postIds = List.of(postId);

            final var commentEntities = List.of(
                CommentEntity.builder().id(UUID.randomUUID()).postId(postId).build(),
                CommentEntity.builder().id(UUID.randomUUID()).postId(postId).build()
            );
            final var commentDtos = commentEntities.stream()
                .map(it -> Comment.newBuilder().id(it.id()).postId(it.postId()).build())
                .toList();

            given(commentRepository.findAllByPostIdIn(any()))
                .willAnswer((call) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((call) -> commentDtos.get(0))
                .willAnswer((call) -> commentDtos.get(1));

            // When
            var result = commentService.getCommentsByPostIds(postIds);

            // Then
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
            // Given
            var postIds = Collections.<UUID>emptyList();

            // When
            var result = commentService.getCommentsByPostIds(postIds);

            // Then
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
            final var input =
                CreateCommentInput.newBuilder()
                    .userId(UUID.randomUUID())
                    .postId(UUID.randomUUID())
                    .content("test-content")
                    .build();

            final var comment = CommentEntity.builder().build();
            final var post = PostRepresentation.builder().build();

            final var expectedComment = Comment.newBuilder().build();

            given(commentMapper.mapToEntity(any()))
                .willReturn(comment);
            given(postRepository.findById(any()))
                .willReturn(Optional.of(post));
            given(userRepository.existsById(any()))
                .willReturn(true);
            given(commentRepository.save(any()))
                .willReturn(comment);
            given(commentMapper.mapToDto(any()))
                .willReturn(expectedComment);

            // When
            final var createCommentResult = commentService.createComment(input);

            // Then
            then(commentMapper)
                .should()
                .mapToEntity(input);
            then(userRepository)
                .should()
                .existsById(input.getUserId());
            then(postRepository)
                .should()
                .findById(input.getPostId());
            then(commentRepository)
                .should()
                .save(comment);
            then(commentMapper)
                .should()
                .mapToDto(comment);

            assertThat(createCommentResult)
                .isNotNull()
                .extracting(CreateCommentResult::getComment)
                .isNotNull()
                .isEqualTo(expectedComment);
        }
    }
}
