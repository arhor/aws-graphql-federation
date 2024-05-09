package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;

import java.util.Set;
import java.util.UUID;

public interface PostService {

    Post findInternalPostRepresentation(UUID postId);

    void createInternalPostRepresentation(Set<? extends UUID> postIds);

    void deleteInternalPostRepresentation(Set<? extends UUID> postIds);
}
