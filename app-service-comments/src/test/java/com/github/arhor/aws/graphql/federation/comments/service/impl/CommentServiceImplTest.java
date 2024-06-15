package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.CommentRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.sorting.CommentsSorted;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.COMMENT;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
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
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.from;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
class CommentServiceImplTest {

    private static final UUID COMMENT_1_ID = ConstantsKt.getTEST_1_UUID_VAL();
    private static final UUID COMMENT_2_ID = ConstantsKt.getTEST_2_UUID_VAL();
    private static final UUID COMMENT_3_ID = ConstantsKt.getTEST_3_UUID_VAL();
    private static final UUID USER_ID = ConstantsKt.getZERO_UUID_VAL();
    private static final UUID POST_ID = ConstantsKt.getOMNI_UUID_VAL();

    private final CommentRepository commentRepository = mock();
    private final CommentMapper commentMapper = mock();
    private final StateGuard stateGuard = mock();

    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(
            commentRepository,
            commentMapper,
            stateGuard
        );
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
            commentRepository,
            commentMapper,
            stateGuard
        );
    }

    @Nested
    @DisplayName("CommentService :: getCommentsReplies")
    class GetCommentsRepliesTest {
        @Test
        void should_return_comments_grouped_by_prnt_id() {
            // Given
            final var commentIds = List.of(COMMENT_1_ID);

            final var commentEntities = List.of(
                CommentEntity.builder().id(COMMENT_2_ID).userId(USER_ID).prntId(COMMENT_1_ID).build(),
                CommentEntity.builder().id(COMMENT_3_ID).userId(USER_ID).prntId(COMMENT_1_ID).build()
            );
            final var commentDtos = commentEntities.stream()
                .map(it -> Comment.newBuilder().id(it.id()).userId(it.userId()).prntId(it.prntId()).build())
                .toList();

            given(commentRepository.findAllByPrntIdIn(any(), any()))
                .willAnswer((__) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((__) -> commentDtos.get(0))
                .willAnswer((__) -> commentDtos.get(1));

            // When
            var result = commentService.getCommentsReplies(commentIds);

            // Then
            then(commentRepository)
                .should()
                .findAllByPrntIdIn(commentIds, CommentsSorted.byCreatedDateTimeAsc());

            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(0));

            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(1));

            assertThat(result)
                .isNotNull()
                .containsOnlyKeys(COMMENT_1_ID)
                .hasEntrySatisfying(COMMENT_1_ID, (comments) ->
                    assertThat(comments)
                        .isNotNull()
                        .containsExactlyElementsOf(commentDtos)
                );
        }

        @Test
        void should_not_interact_with_repository_if_commentIds_is_empty() {
            // Given
            var commentIds = Collections.<UUID>emptyList();

            // When
            var result = commentService.getCommentsReplies(commentIds);

            // Then
            assertThat(result)
                .isNotNull()
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("CommentService :: getCommentsByUserIds")
    class GetCommentsByUserIdsMethodTest {
        @Test
        void should_return_comments_grouped_by_user_id() {
            // Given
            final var userIds = List.of(USER_ID);

            final var commentEntities = List.of(
                CommentEntity.builder().id(COMMENT_1_ID).userId(USER_ID).build(),
                CommentEntity.builder().id(COMMENT_2_ID).userId(USER_ID).build()
            );
            final var commentDtos = commentEntities.stream()
                .map(it -> Comment.newBuilder().id(it.id()).userId(it.userId()).build())
                .toList();

            given(commentRepository.findAllByUserIdIn(any(), any()))
                .willAnswer((__) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((__) -> commentDtos.get(0))
                .willAnswer((__) -> commentDtos.get(1));

            // When
            var result = commentService.getCommentsByUserIds(userIds);

            // Then
            then(commentRepository)
                .should()
                .findAllByUserIdIn(userIds, CommentsSorted.byCreatedDateTimeDesc());

            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(0));

            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(1));

            assertThat(result)
                .isNotNull()
                .containsOnlyKeys(USER_ID)
                .hasEntrySatisfying(USER_ID, (comments) ->
                    assertThat(comments)
                        .isNotNull()
                        .containsExactlyElementsOf(commentDtos)
                );
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
            final var postIds = List.of(POST_ID);

            final var commentEntities = List.of(
                CommentEntity.builder().id(COMMENT_1_ID).postId(POST_ID).build(),
                CommentEntity.builder().id(COMMENT_2_ID).postId(POST_ID).build()
            );
            final var commentDtos = commentEntities.stream()
                .map(it -> Comment.newBuilder().id(it.id()).postId(it.postId()).build())
                .toList();

            given(commentRepository.findAllByPrntIdNullAndPostIdIn(any(), any()))
                .willAnswer((__) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((__) -> commentDtos.get(0))
                .willAnswer((__) -> commentDtos.get(1));

            // When
            var result = commentService.getCommentsByPostIds(postIds);

            // Then
            then(commentRepository)
                .should()
                .findAllByPrntIdNullAndPostIdIn(postIds, CommentsSorted.byCreatedDateTimeAsc());

            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(0));

            then(commentMapper)
                .should()
                .mapToDto(commentEntities.get(1));

            assertThat(result)
                .isNotNull()
                .containsOnlyKeys(POST_ID)
                .hasEntrySatisfying(POST_ID, (comments) ->
                    assertThat(comments)
                        .isNotNull()
                        .containsExactlyElementsOf(commentDtos)
                );
        }

        @Test
        void should_not_interact_with_repository_if_postIds_is_empty() {
            // Given
            var postIds = Collections.<UUID>emptyList();

            // When
            var result = commentService.getCommentsByPostIds(postIds);

            // Then
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
                    .postId(POST_ID)
                    .content("test-content")
                    .build();
            final var comment = CommentEntity.builder().build();
            final var expectedComment = Comment.newBuilder().build();

            given(commentMapper.mapToEntity(any(), any()))
                .willReturn(comment);

            given(commentRepository.save(any()))
                .willReturn(comment);

            given(commentMapper.mapToDto(any()))
                .willReturn(expectedComment);

            // When
            final var createCommentResult = commentService.createComment(input, actor(USER_ID));

            // Then
            then(commentMapper)
                .should()
                .mapToEntity(input, USER_ID);

            then(stateGuard)
                .should()
                .ensureCommentsEnabled(COMMENT.TYPE_NAME, Operation.CREATE, StateGuard.Type.USER, USER_ID);

            then(stateGuard)
                .should()
                .ensureCommentsEnabled(COMMENT.TYPE_NAME, Operation.CREATE, StateGuard.Type.POST, POST_ID);

            then(commentRepository)
                .should()
                .save(comment);

            then(commentMapper)
                .should()
                .mapToDto(comment);

            assertThat(createCommentResult)
                .isNotNull()
                .isEqualTo(expectedComment);
        }
    }

    @Nested
    @DisplayName("CommentService :: updateComment")
    class UpdateCommentMethodTest {
        @Test
        void should_throw_EntityNotFoundException_when_comment_does_not_exist_by_id() {
            // Given
            final var input =
                UpdateCommentInput.newBuilder()
                    .id(COMMENT_1_ID)
                    .build();

            final var expectedEntity = COMMENT.TYPE_NAME;
            final var expectedCondition = COMMENT.Id + " = " + input.getId();
            final var expectedOperation = Operation.UPDATE;

            given(commentRepository.findById(any()))
                .willReturn(Optional.empty());

            // When
            final var result = catchException(() -> commentService.updateComment(input, actor(USER_ID)));

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityNotFoundException.class))
                .returns(expectedEntity, from(EntityNotFoundException::getEntity))
                .returns(expectedCondition, from(EntityNotFoundException::getCondition))
                .returns(expectedOperation, from(EntityNotFoundException::getOperation));
        }

        @Test
        void should_not_call_CommentRepository_save_when_existing_comment_was_not_modified() {
            // Given
            final var input =
                UpdateCommentInput.newBuilder()
                    .id(COMMENT_1_ID)
                    .build();
            final var commentEntity =
                CommentEntity.builder()
                    .userId(USER_ID)
                    .postId(POST_ID)
                    .build();

            final var commentDto = Comment.newBuilder().build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.of(commentEntity));

            given(commentMapper.mapToDto(any()))
                .willReturn(commentDto);

            // When
            final var result = commentService.updateComment(input, actor(USER_ID));

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            then(stateGuard)
                .should()
                .ensureCommentsEnabled(COMMENT.TYPE_NAME, Operation.UPDATE, StateGuard.Type.USER, USER_ID);

            then(stateGuard)
                .should()
                .ensureCommentsEnabled(COMMENT.TYPE_NAME, Operation.UPDATE, StateGuard.Type.POST, POST_ID);

            then(commentMapper)
                .should()
                .mapToDto(commentEntity);

            assertThat(result)
                .isNotNull()
                .isEqualTo(commentDto);
        }
    }

    @Nested
    @DisplayName("CommentService :: deleteComment")
    class DeleteCommentMethodTest {
        @Test
        void should_return_result_with_success_true_when_comment_is_deleted() {
            // Given
            final var comment = CommentEntity.builder().userId(USER_ID).postId(POST_ID).build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.of(comment));

            // When
            final var result = commentService.deleteComment(COMMENT_1_ID, actor(USER_ID));

            // Then
            then(commentRepository)
                .should()
                .findById(COMMENT_1_ID);

            then(stateGuard)
                .should()
                .ensureCommentsEnabled(COMMENT.TYPE_NAME, Operation.DELETE, StateGuard.Type.USER, USER_ID);

            then(stateGuard)
                .should()
                .ensureCommentsEnabled(COMMENT.TYPE_NAME, Operation.DELETE, StateGuard.Type.POST, POST_ID);

            then(commentRepository)
                .should()
                .delete(comment);

            assertThat(result)
                .isTrue();
        }

        @Test
        void should_return_result_with_success_false_when_comment_is_missing_by_id() {
            // Given
            given(commentRepository.findById(any()))
                .willReturn(Optional.empty());

            // When
            final var result = commentService.deleteComment(COMMENT_1_ID, actor(USER_ID));

            // Then
            then(commentRepository)
                .should()
                .findById(COMMENT_1_ID);

            assertThat(result)
                .isFalse();
        }
    }

    private CurrentUserDetails actor(final UUID userId) {
        return new CurrentUserDetails(userId, List.of());
    }
}
