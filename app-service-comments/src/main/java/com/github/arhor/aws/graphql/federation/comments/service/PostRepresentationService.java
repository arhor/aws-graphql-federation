package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for handling post representations.
 */
public interface PostRepresentationService {

    @NotNull
    Map<UUID, Post> findPostsRepresentationsInBatch(@NotNull Set<UUID> postIds);

    /**
     * Creates a new post representation.
     *
     * @param postId the UUID of the post for whom the representation is to be created
     * @param userId the UUID of the user for whom the representation is to be created
     */
    void createPostRepresentation(@NotNull UUID postId, @NotNull UUID userId);

    /**
     * Deletes an existing post representation.
     *
     * @param postId the UUID of the post whose representation is to be deleted
     */
    void deletePostRepresentation(@NotNull UUID postId);

    /**
     * Toggles an ability to create comments for the specified post.
     *
     * @param postId the id of a post
     * @param actor  the user trying to toggle comments on a post
     * @return {@code true} if comments enabled, {@code false} otherwise
     */
    boolean togglePostComments(@NotNull UUID postId, @NotNull CurrentUserDetails actor);
}
