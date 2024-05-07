package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.stream.Stream;

public interface CommentRepository extends ListCrudRepository<CommentEntity, Long> {

    Stream<CommentEntity> findAllByUserIdIn(Collection<Long> userIds);

    Stream<CommentEntity> findAllByPostIdIn(Collection<Long> postIds);
}
