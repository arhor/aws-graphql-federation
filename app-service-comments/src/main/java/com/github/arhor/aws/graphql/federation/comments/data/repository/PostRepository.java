package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface PostRepository extends ListCrudRepository<PostEntity, UUID> {
}
