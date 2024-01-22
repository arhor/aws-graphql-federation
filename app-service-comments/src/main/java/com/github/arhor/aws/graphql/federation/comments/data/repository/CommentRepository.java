package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.stream.Stream;

public interface CommentRepository extends CrudRepository<CommentEntity, Long> {

    Stream<CommentEntity> findAllByUserIdIn(Collection<Long> userIds);

    Stream<CommentEntity> findAllByPostIdIn(Collection<Long> postIds);

    @Modifying
    @Query(name = "CommentEntity.unlinkAllFromUser")
    void unlinkAllFromUser(Long userId);

    @Modifying
    @Query(name = "CommentEntity.deleteAllFromPost")
    void deleteAllFromPost(Long postId);
}
