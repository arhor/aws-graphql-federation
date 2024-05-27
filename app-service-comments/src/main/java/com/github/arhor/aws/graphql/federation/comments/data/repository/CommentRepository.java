package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.List;
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
    List<CommentEntity> findAllByPrntIdIn(Collection<UUID> prntId);

    /**
     * Finds all comment entities for the specified user IDs.
     *
     * @param userIds the collection of user IDs to retrieve comments for
     * @return a stream of comment entities associated with the specified user IDs
     */
    Stream<CommentEntity> findAllByUserIdIn(Collection<UUID> userIds);

    /**
     * Finds all comment entities for the specified post IDs.
     *
     * @param postIds the collection of post IDs to retrieve comments for
     * @return a stream of comment entities associated with the specified post IDs
     */
    Stream<CommentEntity> findAllByPostIdIn(Collection<UUID> postIds);
}
