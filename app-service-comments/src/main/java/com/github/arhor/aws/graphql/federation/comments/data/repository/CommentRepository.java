package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

public interface CommentRepository extends ListCrudRepository<CommentEntity, UUID> {

    Stream<CommentEntity> findAllByUserIdIn(Collection<UUID> userIds);

    Stream<CommentEntity> findAllByPostIdIn(Collection<UUID> postIds);
}
