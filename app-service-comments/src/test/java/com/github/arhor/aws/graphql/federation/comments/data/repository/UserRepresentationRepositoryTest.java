package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation.UserFeature;
import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ExtensionMethod(RepositoryTestExtensions.class)
public class UserRepresentationRepositoryTest extends RepositoryTestBase {

    @Autowired
    private UserRepresentationRepository userRepository;

    @Test
    void should_create_user_with_default_features_set_to_not_null() {
        // Given
        final var userId = userRepository.createTestUser(ConstantsKt.getZERO_UUID_VAL()).id();

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
        final var userId = userRepository.createTestUser(ConstantsKt.getZERO_UUID_VAL(), UserFeature.COMMENTS_DISABLED).id();

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
                it -> assertThat(it).containsExactly(UserFeature.COMMENTS_DISABLED)
            );
    }
}
