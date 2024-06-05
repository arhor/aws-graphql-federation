package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments;
import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments.Feature;
import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostRepresentationRepositoryTest extends RepositoryTestBase {

    @Test
    void should_create_post_with_default_features_set_to_not_null() {
        // Given
        final var postId = createPost().id();

        // When
        final var post = postRepository.findById(postId);

        // Then
        assertThat(post)
            .get()
            .extracting(HasComments::features)
            .extracting(Features::getItems)
            .satisfies(
                it -> assertThat(it).isEmpty()
            );
    }

    @Test
    void should_create_post_with_defined_features_set_to_not_null() {
        // Given
        final var postId = createPost(Feature.COMMENTS_DISABLED).id();

        // When
        final var post = postRepository.findById(postId);

        // Then
        assertThat(post)
            .get()
            .extracting(HasComments::features)
            .extracting(Features::getItems)
            .satisfies(
                it -> assertThat(it).isNotEmpty(),
                it -> assertThat(it).hasSize(1),
                it -> assertThat(it).containsExactly(Feature.COMMENTS_DISABLED)
            );
    }
}
