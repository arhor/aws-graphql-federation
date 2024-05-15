package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepresentationRepository extends CrudRepository<UserRepresentationEntity, UUID> {
}
