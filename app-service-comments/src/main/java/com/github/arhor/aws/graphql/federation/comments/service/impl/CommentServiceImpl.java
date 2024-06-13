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
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.arhor.aws.graphql.federation.starter.security.UtilsKt.ensureAccessAllowed;
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
    public Map<UUID, List<Comment>> getCommentsByUserIds(final Collection<UUID> userIds) {
        return loadAndGroupBy(
            commentRepository::findAllByUserIdIn,
            userIds,
            Comment::getUserId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, List<Comment>> getCommentsByPostIds(final Collection<UUID> postIds) {
        return loadAndGroupBy(
            commentRepository::findAllByPrntIdNullAndPostIdIn,
            postIds,
            Comment::getPostId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, List<Comment>> getCommentsReplies(final Collection<UUID> commentIds) {
        return loadAndGroupBy(
            commentRepository::findAllByPrntIdIn,
            commentIds,
            Comment::getPrntId
        );
    }

    @Override
    public Map<UUID, Integer> getCommentsNumberByPostIds(final Collection<UUID> postIds) {
        return commentRepository.countCommentsByPostIds(postIds);
    }

    @Override
    @Transactional
    public Comment createComment(final CreateCommentInput input, final CurrentUserDetails actor) {
        final var currentOperation = Operation.CREATE;

        ensureOperationAllowed(
            input.getUserId(),
            input.getPostId(),
            input.getPrntId(),
            currentOperation
        );

        var entity = commentMapper.mapToEntity(input);
        var create = commentRepository.save(entity);

        return commentMapper.mapToDto(create);
    }

    @Override
    @Transactional
    public Comment updateComment(final UpdateCommentInput input, final CurrentUserDetails actor) {
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

        ensureOperationAllowed(
            initialState.userId(),
            initialState.postId(),
            currentOperation,
            actor
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
    public boolean deleteComment(final UUID id, final CurrentUserDetails actor) {
        return commentRepository.findById(id)
            .map((comment) -> {
                ensureOperationAllowed(
                    comment.userId(),
                    comment.postId(),
                    Operation.DELETE,
                    actor
                );
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

    private void ensureOperationAllowed(
        final UUID userId,
        final UUID postId,
        final UUID prntId,
        final Operation operation
    ) {
        ensureOperationAllowed(userId, postId, prntId, operation, null);
    }

    private void ensureOperationAllowed(
        final UUID userId,
        final UUID postId,
        final Operation operation,
        final CurrentUserDetails actor
    ) {
        ensureOperationAllowed(userId, postId, null, operation, actor);
    }

    private void ensureOperationAllowed(
        final UUID userId,
        final UUID postId,
        final UUID prntId,
        final Operation operation,
        final CurrentUserDetails actor
    ) {
        if (actor != null) {
            ensureAccessAllowed(userId, actor);
        }
        if (prntId != null) {
            ensureParentCommentExists(postId, prntId, operation);
        }
        ensureCommentsEnabled(userRepository, operation, USER.TYPE_NAME, USER.Id, userId);
        ensureCommentsEnabled(postRepository, operation, POST.TYPE_NAME, POST.Id, postId);
    }

    private void ensureParentCommentExists(
        final UUID postId,
        final UUID prntId,
        final Operation operation
    ) {
        if (commentRepository.existsByPostIdAndPrntId(postId, prntId)) {
            return;
        }
        throw new EntityNotFoundException(
            COMMENT.TYPE_NAME,
            COMMENT.PostId + " = " + postId + " and " + COMMENT.PrntId + " = " + prntId,
            operation
        );
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
     * @param ids        the ids of comments to be loaded
     * @param classifier function that will be used to classify comment for grouping operation
     * @return comments grouped by passed comment classifier
     */
    private Map<UUID, List<Comment>> loadAndGroupBy(
        @Nonnull final Function<Collection<UUID>, Stream<CommentEntity>> dataSource,
        @Nonnull final Collection<UUID> ids,
        @Nonnull final Function<Comment, UUID> classifier
    ) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        try (final var data = dataSource.apply(ids)) {

            final var result =
                data.map(commentMapper::mapToDto)
                    .collect(groupingBy(classifier));

            log.debug("Loaded grouped data of size {} for {} ids", result.size(), ids.size());

            return result;
        }
    }
}
