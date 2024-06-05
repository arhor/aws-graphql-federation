package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments;
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
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
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
    public Comment getCommentById(final UUID id) {
        return commentRepository.findById(id)
            .map(commentMapper::mapToDto)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    COMMENT.TYPE_NAME,
                    COMMENT.Id + " = " + id,
                    Operation.LOOKUP
                )
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, List<Comment>> getCommentsReplies(final Collection<UUID> commentIds) {
        if (commentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        final var result = new HashMap<UUID, List<Comment>>(commentIds.size());

        try (final var replies = commentRepository.findAllByPrntIdIn(commentIds)) {
            replies.forEach(comment -> {
                final var group = result.computeIfAbsent(comment.prntId(), (__) -> new ArrayList<>());
                final var reply = commentMapper.mapToDto(comment);

                group.add(reply);
            });
        }
        return result;
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
    public Comment createComment(final CreateCommentInput input) {
        final var currentOperation = Operation.CREATE;

        ensureUserAndPostCommentsEnabled(
            input.getUserId(),
            input.getPostId(),
            currentOperation
        );

        var entity = commentMapper.mapToEntity(input);
        var create = commentRepository.save(entity);

        return commentMapper.mapToDto(create);
    }

    @Override
    @Transactional
    public Comment updateComment(final UpdateCommentInput input) {
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

        ensureUserAndPostCommentsEnabled(
            initialState.userId(),
            initialState.postId(),
            currentOperation
        );

        final var currentState = determineCurrentState(initialState, input);

        final var entity =
            initialState.equals(currentState)
                ? currentState
                : trySaveHandlingConcurrentUpdates(currentState);

        return commentMapper.mapToDto(entity);
    }

    @Override
    @Transactional
    public boolean deleteComment(final DeleteCommentInput input) {
        return commentRepository.findById(input.getId())
            .map((comment) -> {
                commentRepository.delete(comment);
                return true;
            })
            .orElse(false);
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

    private void ensureUserAndPostCommentsEnabled(
        final UUID userId,
        final UUID postId,
        final Operation operation
    ) {
        ensureCommentsEnabled(userRepository, operation, USER.TYPE_NAME, USER.Id, userId);
        ensureCommentsEnabled(postRepository, operation, POST.TYPE_NAME, POST.Id, postId);
    }

    private <T extends HasComments> void ensureCommentsEnabled(
        final CrudRepository<T, UUID> commentsContainerSource,
        final Operation operation,
        final String entity,
        final String field,
        final UUID id
    ) {
        final var commentsContainer =
            commentsContainerSource.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        COMMENT.TYPE_NAME,
                        entity + " with " + field + " = " + id + " is not found",
                        operation
                    )
                );

        if (commentsContainer.features().check(HasComments.Feature.COMMENTS_DISABLED)) {
            throw new EntityOperationRestrictedException(
                COMMENT.TYPE_NAME,
                "Comments disabled for the " + entity + " with " + field + " = " + id,
                operation
            );
        }
    }

    private CommentEntity trySaveHandlingConcurrentUpdates(final CommentEntity entity) {
        try {
            return commentRepository.save(entity);
        } catch (final OptimisticLockingFailureException e) {
            log.error(e.getMessage(), e);

            throw new EntityOperationRestrictedException(
                COMMENT.TYPE_NAME,
                COMMENT.Id + " = " + entity.id() + "(updated concurrently)",
                Operation.UPDATE,
                e
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
