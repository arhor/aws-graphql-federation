package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments;
import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments.Feature;
import com.github.arhor.aws.graphql.federation.spring.core.data.Features;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepresentationRepositoryTest extends RepositoryTestBase {

    @Test
    void should_create_user_with_default_features_set_to_not_null() {
        // Given
        final var userId = createUser().id();

        // When
        final var user = userRepository.findById(userId);

        // Then
        assertThat(user)
            .get()
            .extracting(HasComments::features)
            .extracting(Features::getItems)
            .satisfies(
                it -> assertThat(it).isEmpty()
            );
    }

    @Test
    void should_create_user_with_defined_features_set_to_not_null() {
        // Given
        final var userId = createUser(Feature.COMMENTS_DISABLED).id();

        // When
        final var user = userRepository.findById(userId);

        // Then
        assertThat(user)
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
