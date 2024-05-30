package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.CommentRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.COMMENT;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.DeleteCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
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

    private static final UUID COMMENT_1_ID = UUID.randomUUID();
    private static final UUID COMMENT_2_ID = UUID.randomUUID();
    private static final UUID COMMENT_3_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID POST_ID = UUID.randomUUID();

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
                .map(it -> Comment.newBuilder().id(it.id()).userId(it.userId()).build())
                .toList();

            given(commentRepository.findAllByPrntIdIn(any()))
                .willAnswer((__) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((__) -> commentDtos.get(0))
                .willAnswer((__) -> commentDtos.get(1));

            // When
            var result = commentService.getCommentsReplies(commentIds);

            // Then
            then(commentRepository)
                .should()
                .findAllByPrntIdIn(commentIds);

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

            given(commentRepository.findAllByUserIdIn(any()))
                .willAnswer((__) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((__) -> commentDtos.get(0))
                .willAnswer((__) -> commentDtos.get(1));

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

            given(commentRepository.findAllByPostIdIn(any()))
                .willAnswer((__) -> commentEntities.stream());

            given(commentMapper.mapToDto(any()))
                .willAnswer((__) -> commentDtos.get(0))
                .willAnswer((__) -> commentDtos.get(1));

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
                    .userId(USER_ID)
                    .postId(POST_ID)
                    .content("test-content")
                    .build();

            final var comment = CommentEntity.builder().build();
            final var user = UserRepresentation.builder().build();
            final var post = PostRepresentation.builder().build();

            final var expectedComment = Comment.newBuilder().build();

            given(commentMapper.mapToEntity(any()))
                .willReturn(comment);

            given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

            given(postRepository.findById(any()))
                .willReturn(Optional.of(post));

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
                .findById(input.getUserId());

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
            final var result = catchException(() -> commentService.updateComment(input));

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
        void should_throw_EntityNotFoundException_when_user_does_not_exist_by_id() {
            // Given
            final var input =
                UpdateCommentInput.newBuilder()
                    .id(COMMENT_1_ID)
                    .build();
            final var comment =
                CommentEntity.builder()
                    .userId(USER_ID)
                    .postId(POST_ID)
                    .build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.of(comment));

            given(userRepository.findById(any()))
                .willReturn(Optional.empty());

            final var expectedEntity = COMMENT.TYPE_NAME;
            final var expectedCondition = USER.TYPE_NAME + " with " + USER.Id + " = " + USER_ID + " is not found";
            final var expectedOperation = Operation.UPDATE;

            // When
            final var result = catchException(() -> commentService.updateComment(input));

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            then(userRepository)
                .should()
                .findById(USER_ID);

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityNotFoundException.class))
                .returns(expectedEntity, from(EntityNotFoundException::getEntity))
                .returns(expectedCondition, from(EntityNotFoundException::getCondition))
                .returns(expectedOperation, from(EntityNotFoundException::getOperation));
        }

        @Test
        void should_throw_EntityOperationRestrictedException_when_user_comments_disabled() {
            // Given
            final var input =
                UpdateCommentInput.newBuilder()
                    .id(COMMENT_1_ID)
                    .build();
            final var comment = CommentEntity.builder()
                .userId(USER_ID)
                .postId(POST_ID)
                .build();

            final var user = UserRepresentation.builder().commentsDisabled(true).build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.of(comment));

            given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

            final var expectedEntity = COMMENT.TYPE_NAME;
            final var expectedCondition = "Comments disabled for the " + USER.TYPE_NAME + " with " + USER.Id + " = " + USER_ID;
            final var expectedOperation = Operation.UPDATE;

            // When
            final var result = catchException(() -> commentService.updateComment(input));

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            then(userRepository)
                .should()
                .findById(USER_ID);

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityOperationRestrictedException.class))
                .returns(expectedEntity, from(EntityOperationRestrictedException::getEntity))
                .returns(expectedCondition, from(EntityOperationRestrictedException::getCondition))
                .returns(expectedOperation, from(EntityOperationRestrictedException::getOperation));
        }

        @Test
        void should_throw_EntityNotFoundException_when_post_does_not_exist_by_id() {
            // Given
            final var input =
                UpdateCommentInput.newBuilder()
                    .id(COMMENT_1_ID)
                    .build();
            final var comment =
                CommentEntity.builder()
                    .userId(USER_ID)
                    .postId(POST_ID)
                    .build();

            final var user = UserRepresentation.builder().build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.of(comment));

            given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

            given(postRepository.findById(any()))
                .willReturn(Optional.empty());

            final var expectedEntity = COMMENT.TYPE_NAME;
            final var expectedCondition = POST.TYPE_NAME + " with " + POST.Id + " = " + POST_ID + " is not found";
            final var expectedOperation = Operation.UPDATE;

            // When
            final var result = catchException(() -> commentService.updateComment(input));

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            then(userRepository)
                .should()
                .findById(USER_ID);

            then(postRepository)
                .should()
                .findById(POST_ID);

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityNotFoundException.class))
                .returns(expectedEntity, from(EntityNotFoundException::getEntity))
                .returns(expectedCondition, from(EntityNotFoundException::getCondition))
                .returns(expectedOperation, from(EntityNotFoundException::getOperation));
        }

        @Test
        void should_throw_EntityOperationRestrictedException_when_post_comments_disabled() {
            // Given
            final var input =
                UpdateCommentInput.newBuilder()
                    .id(COMMENT_1_ID)
                    .build();
            final var comment = CommentEntity.builder()
                .userId(USER_ID)
                .postId(POST_ID)
                .build();

            final var user = UserRepresentation.builder().build();
            final var post = PostRepresentation.builder().commentsDisabled(true).build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.of(comment));

            given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

            given(postRepository.findById(any()))
                .willReturn(Optional.of(post));

            final var expectedEntity = COMMENT.TYPE_NAME;
            final var expectedCondition = "Comments disabled for the " + POST.TYPE_NAME + " with " + POST.Id + " = " + POST_ID;
            final var expectedOperation = Operation.UPDATE;

            // When
            final var result = catchException(() -> commentService.updateComment(input));

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            then(userRepository)
                .should()
                .findById(USER_ID);

            then(postRepository)
                .should()
                .findById(POST_ID);

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityOperationRestrictedException.class))
                .returns(expectedEntity, from(EntityOperationRestrictedException::getEntity))
                .returns(expectedCondition, from(EntityOperationRestrictedException::getCondition))
                .returns(expectedOperation, from(EntityOperationRestrictedException::getOperation));
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
            final var user = UserRepresentation.builder().build();
            final var post = PostRepresentation.builder().build();
            final var commentDto = Comment.newBuilder().build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.of(commentEntity));

            given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

            given(postRepository.findById(any()))
                .willReturn(Optional.of(post));

            given(commentMapper.mapToDto(any()))
                .willReturn(commentDto);

            // When
            final var result = commentService.updateComment(input);

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            then(userRepository)
                .should()
                .findById(USER_ID);

            then(postRepository)
                .should()
                .findById(POST_ID);

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
            final var input = DeleteCommentInput.newBuilder().id(COMMENT_1_ID).build();
            final var comment = CommentEntity.builder().build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.of(comment));

            // When
            final var result = commentService.deleteComment(input);

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            then(commentRepository)
                .should()
                .delete(comment);

            assertThat(result)
                .isTrue();
        }

        @Test
        void should_return_result_with_success_false_when_comment_is_missing_by_id() {
            // Given
            final var input = DeleteCommentInput.newBuilder().id(COMMENT_1_ID).build();

            given(commentRepository.findById(any()))
                .willReturn(Optional.empty());

            // When
            final var result = commentService.deleteComment(input);

            // Then
            then(commentRepository)
                .should()
                .findById(input.getId());

            assertThat(result)
                .isFalse();
        }
    }
}
