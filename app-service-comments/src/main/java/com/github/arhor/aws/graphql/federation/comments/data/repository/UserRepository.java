package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.UserEntity;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<UserEntity, Long> {
}
