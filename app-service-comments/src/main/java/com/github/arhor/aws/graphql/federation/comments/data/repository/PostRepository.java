package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostEntity;
import org.springframework.data.repository.ListCrudRepository;

public interface PostRepository extends ListCrudRepository<PostEntity, Long> {
}
