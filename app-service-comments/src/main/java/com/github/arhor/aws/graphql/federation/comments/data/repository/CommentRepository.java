package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.mapping.CommentsNumberByPostIdResultSetExtractor;
import jakarta.annotation.Nonnull;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Repository interface for handling CRUD operations for comment entities.
 */
public interface CommentRepository extends ListCrudRepository<CommentEntity, UUID> {

    /**
     * Finds all comment entities for the specified parent comment IDs.
     *
     * @param prntId the collection of parent comment IDs to retrieve comments for
     * @return a stream of comment entities associated with the specified parent comment IDs
     */
    Stream<CommentEntity> findAllByPrntIdIn(@Nonnull Collection<UUID> prntId);

    /**
     * Finds all comment entities for the specified user IDs.
     *
     * @param userIds the collection of user IDs to retrieve comments for
     * @return a stream of comment entities associated with the specified user IDs
     */
    Stream<CommentEntity> findAllByUserIdIn(@Nonnull Collection<UUID> userIds);

    /**
     * Finds all top-level (without a parent) comment entities for the specified post IDs.
     *
     * @param postIds the collection of post IDs to retrieve comments for
     * @return a stream of top-level comment entities associated with the specified post IDs
     */
    Stream<CommentEntity> findAllByPrntIdNullAndPostIdIn(@Nonnull Collection<UUID> postIds);

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
    Map<UUID, Integer> countCommentsByPostIds(@Nonnull Collection<UUID> postIds);
}
