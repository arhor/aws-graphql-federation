package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;

import java.util.UUID;

public interface PostRepresentationService {

    Post findPostRepresentation(UUID postId);

    void createPostRepresentation(UUID postId, UUID idempotencyKey);

    void deletePostRepresentation(UUID postId, UUID idempotencyKey);
}
