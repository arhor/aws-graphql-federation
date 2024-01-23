package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.CommentRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.COMMENT;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<Comment>> getCommentsByUserIds(final Collection<Long> userIds) {
        return findCommentsThenGroupBy(
            userIds,
            commentRepository::findAllByUserIdIn,
            Comment::getUserId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<Comment>> getCommentsByPostIds(final Collection<Long> postIds) {
        return findCommentsThenGroupBy(
            postIds,
            commentRepository::findAllByPostIdIn,
            Comment::getPostId
        );
    }

    @Override
    @Transactional
    public CreateCommentResult createComment(final CreateCommentInput input) {
        var entity = commentMapper.mapToEntity(input);
        var create = commentRepository.save(entity);
        var result = commentMapper.mapToDto(create);

        return new CreateCommentResult(result);
    }

    @Override
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public UpdateCommentResult updateComment(final UpdateCommentInput input) {
        var initialState = commentRepository
            .findById(input.getId())
            .orElseThrow(() -> new EntityNotFoundException(
                COMMENT.TYPE_NAME,
                COMMENT.Id + " = " + input.getId(),
                Operation.UPDATE
            ));

        var currentState = initialState;

        if (input.getContent() != null) {
            currentState = currentState.toBuilder()
                .content(input.getContent())
                .build();
        }
        var entity =
            (currentState != initialState)
                ? commentRepository.save(currentState)
                : initialState;

        var comment = commentMapper.mapToDto(entity);

        return new UpdateCommentResult(comment);
    }

    @Override
    @Transactional
    public boolean deleteComment(final long id) {
        return commentRepository.findById(id)
            .map((comment) -> {
                commentRepository.delete(comment);
                return true;
            })
            .orElse(false);
    }

    @Override
    @Transactional
    public void unlinkUserComments(final long userId) {
        commentRepository.unlinkAllFromUser(userId);
    }

    @Override
    @Transactional
    public void deletePostComments(final long postId) {
        commentRepository.deleteAllFromPost(postId);
    }

    /**
     * @param ids        ids of the comments
     * @param dataSource function that will be used to load comments in case ids collection is not empty
     * @param classifier function that will be used to classify object for grouping operation
     */
    private <K> Map<K, List<Comment>> findCommentsThenGroupBy(
        final Collection<K> ids,
        final Function<Collection<K>, Stream<CommentEntity>> dataSource,
        final Function<Comment, K> classifier
    ) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        try (var data = dataSource.apply(ids)) {
            return data
                .map(commentMapper::mapToDto)
                .collect(groupingBy(classifier));
        }
    }
}
