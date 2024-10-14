package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation.PostFeature;
import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ExtensionMethod(RepositoryTestExtensions.class)
public class PostRepresentationRepositoryTest extends RepositoryTestBase {

    @Autowired
    private PostRepresentationRepository postRepository;

    @Test
    void should_create_post_with_default_features_set_to_not_null() {
        // Given
        final var postId = postRepository.createTestPost(ConstantsKt.getOMNI_UUID_VAL()).id();

        // When
        final var post = postRepository.findById(postId);

        // Then
        assertThat(post)
            .get()
            .extracting(PostRepresentation::features)
            .extracting(Features::getItems)
            .satisfies(it -> assertThat(it).isEmpty());
    }

    @Test
    void should_create_post_with_defined_features_set_to_not_null() {
        // Given
        final var postId = postRepository.createTestPost(ConstantsKt.getOMNI_UUID_VAL(), PostFeature.COMMENTS_DISABLED).id();

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
