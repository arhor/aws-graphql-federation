package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.CommentRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.COMMENT;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.DeleteCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.DeleteCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import com.github.arhor.aws.graphql.federation.common.exception.EntityCannotBeUpdatedException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Trace
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepresentationRepository postRepository;
    private final UserRepresentationRepository userRepository;

    private GroupingLoader<CommentEntity, Comment, UUID> usersCommentsLoader;
    private GroupingLoader<CommentEntity, Comment, UUID> postsCommentsLoader;

    @PostConstruct
    public void initialize() {
        usersCommentsLoader = new GroupingLoader<>(
            commentRepository::findAllByUserIdIn,
            commentMapper::mapToDto,
            Comment::getUserId
        );
        postsCommentsLoader = new GroupingLoader<>(
            commentRepository::findAllByPostIdIn,
            commentMapper::mapToDto,
            Comment::getPostId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, List<Comment>> getCommentsByUserIds(final Collection<UUID> userIds) {
        return usersCommentsLoader.loadBy(userIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, List<Comment>> getCommentsByPostIds(final Collection<UUID> postIds) {
        return postsCommentsLoader.loadBy(postIds);
    }

    @Override
    @Transactional
    public CreateCommentResult createComment(final CreateCommentInput input) {
        final var currentOperation = Operation.CREATE;

        ensureUserExists(input.getUserId(), currentOperation);
        ensureCommentsEnabled(input.getPostId(), currentOperation);

        var entity = commentMapper.mapToEntity(input);
        var create = commentRepository.save(entity);
        var result = commentMapper.mapToDto(create);

        return new CreateCommentResult(result);
    }

    @Override
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public UpdateCommentResult updateComment(final UpdateCommentInput input) {
        final var currentOperation = Operation.UPDATE;
        final var commentId = input.getId();
        final var initialState =
            commentRepository.findById(commentId)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        COMMENT.TYPE_NAME,
                        COMMENT.Id + " = " + commentId,
                        currentOperation
                    )
                );

        ensureUserExists(initialState.userId(), currentOperation);
        ensureCommentsEnabled(initialState.postId(), currentOperation);

        final var currentState = determineCurrentState(initialState, input);

        final var entity =
            initialState.equals(currentState)
                ? currentState
                : commentRepository.save(currentState);

        final var comment = commentMapper.mapToDto(entity);

        return new UpdateCommentResult(comment);
    }

    @Override
    @Transactional
    public DeleteCommentResult deleteComment(final DeleteCommentInput input) {
        final var result = commentRepository.findById(input.getId())
            .map((comment) -> {
                commentRepository.delete(comment);
                return true;
            })
            .orElse(false);

        return new DeleteCommentResult(result);
    }

    private CommentEntity determineCurrentState(
        final CommentEntity initialState,
        final UpdateCommentInput input
    ) {
        final var currentStateBuilder = initialState.toBuilder();
        {
            final var content = input.getContent();
            if (content != null) {
                currentStateBuilder.content(content);
            }
        }
        return currentStateBuilder.build();
    }

    private void ensureCommentsEnabled(final UUID postId, final Operation operation) {
        final var post =
            postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(
                        COMMENT.TYPE_NAME,
                        POST.TYPE_NAME + " with " + POST.Id + " = " + postId + " is not found",
                        operation
                    )
                );

        if (post.commentsDisabled()) {
            throw new EntityCannotBeUpdatedException(
                COMMENT.TYPE_NAME,
                "Comments disabled for the " + POST.TYPE_NAME + " with " + POST.Id + " = " + postId
            );
        }
    }

    private void ensureUserExists(final UUID userId, final Operation operation) {
        final var userExists = userRepository.existsById(userId);

        if (!userExists) {
            throw new EntityNotFoundException(
                COMMENT.TYPE_NAME,
                USER.TYPE_NAME + " with " + USER.Id + " = " + userId + " is not found",
                operation
            );
        }
    }

    /**
     * @param dataSource function that will be used to load comments in case ids collection is not empty
     * @param dataMapper function that converts entities of type T to type D
     * @param classifier function that will be used to classify object for grouping operation
     * @param <T>        entity type loaded by datasource
     * @param <D>        type of output objects
     * @param <K>        type of key
     */
    private record GroupingLoader<T, D, K>(
        Function<Collection<K>, Stream<T>> dataSource,
        Function<T, D> dataMapper,
        Function<D, K> classifier
    ) {
        Map<K, List<D>> loadBy(final Collection<K> ids) {
            if (ids.isEmpty()) {
                return Collections.emptyMap();
            }
            try (var data = dataSource.apply(ids)) {
                return data
                    .map(dataMapper)
                    .collect(groupingBy(classifier));
            }
        }
    }
}
