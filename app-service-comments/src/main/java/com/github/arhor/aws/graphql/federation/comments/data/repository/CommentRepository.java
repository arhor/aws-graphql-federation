package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.model.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.mapping.CommentsNumberByPostIdResultSetExtractor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Repository interface for handling CRUD operations for comment entities.
 */
public interface CommentRepository extends
    ListCrudRepository<CommentEntity, UUID>,
    ListPagingAndSortingRepository<CommentEntity, UUID> {

    /**
     * Finds all comments for specified users by their IDs.
     *
     * @param userIds collection of user IDs to retrieve comments for
     * @param sort    order which should be applied to result, provide {@code Sort.unsorted()} if not needed
     * @return a stream of comment entities associated with the specified user IDs
     */
    @NotNull
    Stream<CommentEntity> findAllByUserIdIn(@NotNull Collection<UUID> userIds, @NotNull Sort sort);

    /**
     * Finds all replies for specified parent comments by their IDs.
     *
     * @param prntIds the collection of parent comment IDs to retrieve comments for
     * @param sort    order which should be applied to result, provide {@code Sort.unsorted()} if not needed
     * @return a stream of comment entities associated with the specified parent comment IDs
     */
    @NotNull
    Stream<CommentEntity> findAllByPrntIdIn(@NotNull Collection<UUID> prntIds, @NotNull Sort sort);

    /**
     * Finds all top-level (without a parent) comments for specified posts by their IDs.
     *
     * @param postIds the collection of post IDs to retrieve comments for
     * @param sort    order which should be applied to result, provide {@code Sort.unsorted()} if not needed
     * @return a stream of top-level comment entities associated with the specified post IDs
     */
    @NotNull
    Stream<CommentEntity> findAllByPrntIdNullAndPostIdIn(@NotNull Collection<UUID> postIds, @NotNull Sort sort);

    /**
     * Checks if a comment exists with the given post ID and parent ID.
     *
     * @param postId the UUID of the post to check for existence.
     * @param prntId the UUID of the parent comment to check for existence.
     * @return {@code true} if a comment exists with the given post ID and parent ID, {@code false} otherwise.
     */
    boolean existsByPostIdAndPrntId(@NotNull UUID postId, @NotNull UUID prntId);

    /**
     * Counts all comments for the specified post IDs.
     *
     * @param postIds the collection of post IDs to retrieve number of comments for
     * @return a map where the key is the post ID and the value is a number of comments associated with that post
     */
    @Query(
        name = "CommentEntity.countCommentsByPostIds",
        resultSetExtractorRef = CommentsNumberByPostIdResultSetExtractor.BEAN_NAME
    )
    @NotNull
    Map<UUID, Integer> countCommentsByPostIds(@NotNull Collection<UUID> postIds);
}
