package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;

import java.util.Set;

public interface PostService {

    Post findInternalPostRepresentation(Long postId);

    void createInternalPostRepresentation(Long postId);

    void createInternalPostRepresentation(Set<? extends Long> postIds);

    void deleteInternalPostRepresentation(Long postId);

    void deleteInternalPostRepresentation(Set<? extends Long> postIds);
}
