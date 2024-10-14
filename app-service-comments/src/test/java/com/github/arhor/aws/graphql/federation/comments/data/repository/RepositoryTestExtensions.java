package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.model.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation;

import java.util.EnumSet;
import java.util.UUID;

final class RepositoryTestExtensions {

    private RepositoryTestExtensions() {
        throw new UnsupportedOperationException("Cannot be instantiated");
    }

    public static UserRepresentation createTestUser(
        final UserRepresentationRepository userRepository,
        final UUID id
    ) {
        return userRepository.save(
            UserRepresentation.builder()
                .id(id)
                .shouldBePersisted(true)
                .build()
        );
    }

    public static UserRepresentation createTestUser(
        final UserRepresentationRepository userRepository,
        final UUID id,
        final UserRepresentation.UserFeature feature,
        final UserRepresentation.UserFeature... features
    ) {
        return userRepository.save(
            UserRepresentation.builder()
                .id(id)
                .features(new UserRepresentation.UserFeatures(EnumSet.of(feature, features)))
                .shouldBePersisted(true)
                .build()
        );
    }

    public static PostRepresentation createTestPost(
        final PostRepresentationRepository postRepository,
        final UUID id
    ) {
        return postRepository.save(
            PostRepresentation.builder()
                .id(id)
                .shouldBePersisted(true)
                .build()
        );
    }

    public static PostRepresentation createTestPost(
        final PostRepresentationRepository postRepository,
        final UUID id,
        final PostRepresentation.PostFeature feature,
        final PostRepresentation.PostFeature... features
    ) {
        return postRepository.save(
            PostRepresentation.builder()
                .id(id)
                .features(new PostRepresentation.PostFeatures(feature, features))
                .shouldBePersisted(true)
                .build()
        );
    }

    public static CommentEntity createCommentComment(
        final CommentRepository commentRepository,
        final UserRepresentation user,
        final PostRepresentation post
    ) {
        return createCommentComment(commentRepository, user, post, null);
    }

    public static CommentEntity createCommentComment(
        final CommentRepository commentRepository,
        final UserRepresentation user,
        final PostRepresentation post,
        final CommentEntity parent
    ) {
        return commentRepository.save(
            CommentEntity.builder()
                .userId(user.id())
                .postId(post.id())
                .prntId((parent != null) ? parent.id() : null)
                .content("user-" + user.id() + " / post-" + post.id())
                .build()
        );
    }
}
