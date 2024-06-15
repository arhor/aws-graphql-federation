package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.Commentable;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepresentationRepositoryTest extends RepositoryTestBase {

    @Test
    void should_create_user_with_default_features_set_to_not_null() {
        // Given
        final var userId = createUser(ConstantsKt.getZERO_UUID_VAL()).id();

        // When
        final var user = userRepository.findById(userId);

        // Then
        assertThat(user)
            .get()
            .extracting(UserRepresentation::features)
            .extracting(Features::getItems)
            .satisfies(
                it -> assertThat(it).isEmpty()
            );
    }

    @Test
    void should_create_user_with_defined_features_set_to_not_null() {
        // Given
        final var userId = createUser(ConstantsKt.getZERO_UUID_VAL(), Commentable.Feature.COMMENTS_DISABLED).id();

        // When
        final var user = userRepository.findById(userId);

        // Then
        assertThat(user)
            .get()
            .extracting(UserRepresentation::features)
            .extracting(Features::getItems)
            .satisfies(
                it -> assertThat(it).isNotEmpty(),
                it -> assertThat(it).hasSize(1),
                it -> assertThat(it).containsExactly(Commentable.Feature.COMMENTS_DISABLED)
            );
    }
}
