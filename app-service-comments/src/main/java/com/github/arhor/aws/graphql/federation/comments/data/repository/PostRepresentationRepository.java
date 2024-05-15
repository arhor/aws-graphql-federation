package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PostRepresentationRepository extends CrudRepository<PostRepresentationEntity, UUID> {
}
