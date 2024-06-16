package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation.PostFeature;
import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostRepresentationRepositoryTest extends RepositoryTestBase {

    @Test
    void should_create_post_with_default_features_set_to_not_null() {
        // Given
        final var postId = createPost(ConstantsKt.getOMNI_UUID_VAL()).id();

        // When
        final var post = postRepository.findById(postId);

        // Then
        assertThat(post)
            .get()
            .extracting(PostRepresentation::features)
            .extracting(Features::getItems)
            .satisfies(
                it -> assertThat(it).isEmpty()
            );
    }

    @Test
    void should_create_post_with_defined_features_set_to_not_null() {
        // Given
        final var postId = createPost(ConstantsKt.getOMNI_UUID_VAL(), PostFeature.COMMENTS_DISABLED).id();

        // When
        final var post = postRepository.findById(postId);

        // Then
        assertThat(post)
            .get()
            .extracting(PostRepresentation::features)
            .extracting(Features::getItems)
            .satisfies(
                it -> assertThat(it).isNotEmpty(),
                it -> assertThat(it).hasSize(1),
                it -> assertThat(it).containsExactly(PostFeature.COMMENTS_DISABLED)
            );
    }
}
