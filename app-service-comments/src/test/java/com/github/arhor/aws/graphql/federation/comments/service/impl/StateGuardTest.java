package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation.PostFeature;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation.PostFeatures;
import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation.UserFeature;
import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation.UserFeatures;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.COMMENT;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.from;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class StateGuardTest {

    private static UUID USER_ID = ConstantsKt.getZERO_UUID_VAL();
    private static UUID POST_ID = ConstantsKt.getOMNI_UUID_VAL();

    private final PostRepresentationRepository postRepository = mock();
    private final UserRepresentationRepository userRepository = mock();

    private final StateGuard stateGuard = new StateGuard(postRepository, userRepository);

    @BeforeAll
    static void setupClass() {
        USER_ID = ConstantsKt.getZERO_UUID_VAL();
        POST_ID = ConstantsKt.getOMNI_UUID_VAL();
    }

    @AfterAll
    static void closeClass() {
        USER_ID = null;
        POST_ID = null;
    }

    @Test
    void should_throw_EntityNotFoundException_when_user_does_not_exist_by_id() {
        // Given
        given(userRepository.findById(any()))
            .willReturn(Optional.empty());

        final var expectedId = USER_ID;
        final var expectedEntity = COMMENT.TYPE_NAME;
        final var expectedCondition = USER.TYPE_NAME + " with " + USER.Id + " = " + expectedId + " is not found";
        final var expectedOperation = Operation.UNKNOWN;

        // When
        final var result = catchException(
            () -> stateGuard.ensureCommentsEnabled(
                expectedEntity,
                expectedOperation,
                StateGuard.Type.USER,
                expectedId
            )
        );

        // Then
        then(userRepository)
            .should()
            .findById(expectedId);

        assertThat(result)
            .isNotNull()
            .asInstanceOf(type(EntityNotFoundException.class))
            .returns(expectedEntity, from(EntityNotFoundException::getEntity))
            .returns(expectedCondition, from(EntityNotFoundException::getCondition))
            .returns(expectedOperation, from(EntityNotFoundException::getOperation));
    }

    @Test
    void should_throw_EntityOperationRestrictedException_when_user_comments_disabled() {
        // Given
        final var expectedId = USER_ID;
        final var user = UserRepresentation.builder()
            .id(expectedId)
            .features(new UserFeatures(EnumSet.of(UserFeature.COMMENTS_DISABLED)))
            .build();

        given(userRepository.findById(any()))
            .willReturn(Optional.of(user));

        final var expectedEntity = COMMENT.TYPE_NAME;
        final var expectedCondition = "Comments disabled for the " + USER.TYPE_NAME + " with " + USER.Id + " = " + expectedId;
        final var expectedOperation = Operation.UNKNOWN;

        // When
        final var result = catchException(
            () -> stateGuard.ensureCommentsEnabled(
                expectedEntity,
                expectedOperation,
                StateGuard.Type.USER,
                expectedId
            )
        );

        // Then
        then(userRepository)
            .should()
            .findById(expectedId);

        assertThat(result)
            .isNotNull()
            .asInstanceOf(type(EntityOperationRestrictedException.class))
            .returns(expectedEntity, from(EntityOperationRestrictedException::getEntity))
            .returns(expectedCondition, from(EntityOperationRestrictedException::getCondition))
            .returns(expectedOperation, from(EntityOperationRestrictedException::getOperation));
    }

    @Test
    void should_throw_EntityNotFoundException_when_post_does_not_exist_by_id() {
        // Given
        given(postRepository.findById(any()))
            .willReturn(Optional.empty());

        final var expectedId = POST_ID;
        final var expectedEntity = COMMENT.TYPE_NAME;
        final var expectedCondition = POST.TYPE_NAME + " with " + POST.Id + " = " + expectedId + " is not found";
        final var expectedOperation = Operation.UNKNOWN;

        // When
        final var result = catchException(
            () -> stateGuard.ensureCommentsEnabled(
                expectedEntity,
                expectedOperation,
                StateGuard.Type.POST,
                expectedId
            )
        );

        // Then
        then(postRepository)
            .should()
            .findById(expectedId);

        assertThat(result)
            .isNotNull()
            .asInstanceOf(type(EntityNotFoundException.class))
            .returns(expectedEntity, from(EntityNotFoundException::getEntity))
            .returns(expectedCondition, from(EntityNotFoundException::getCondition))
            .returns(expectedOperation, from(EntityNotFoundException::getOperation));
    }

    @Test
    void should_throw_EntityOperationRestrictedException_when_post_comments_disabled() {
        // Given
        final var post = PostRepresentation.builder()
            .features(new PostFeatures(PostFeature.COMMENTS_DISABLED))
            .build();

        given(postRepository.findById(any()))
            .willReturn(Optional.of(post));

        final var expectedId = POST_ID;
        final var expectedEntity = COMMENT.TYPE_NAME;
        final var expectedCondition = "Comments disabled for the " + POST.TYPE_NAME + " with " + POST.Id + " = " + expectedId;
        final var expectedOperation = Operation.UNKNOWN;

        // When
        final var result = catchException(
            () -> stateGuard.ensureCommentsEnabled(
                expectedEntity,
                expectedOperation,
                StateGuard.Type.POST,
                expectedId
            )
        );

        // Then
        then(postRepository)
            .should()
            .findById(expectedId);

        assertThat(result)
            .isNotNull()
            .asInstanceOf(type(EntityOperationRestrictedException.class))
            .returns(expectedEntity, from(EntityOperationRestrictedException::getEntity))
            .returns(expectedCondition, from(EntityOperationRestrictedException::getCondition))
            .returns(expectedOperation, from(EntityOperationRestrictedException::getOperation));
    }

    @Test
    void should_not_throw_any_when_user_comments_enabled() {
        // Given
        final var user = UserRepresentation.builder().id(USER_ID).build();
        final var expectedId = user.id();
        final var expectedEntity = COMMENT.TYPE_NAME;
        final var expectedOperation = Operation.UNKNOWN;

        given(userRepository.findById(any()))
            .willReturn(Optional.of(user));

        // When
        final var result = catchException(
            () -> stateGuard.ensureCommentsEnabled(
                expectedEntity,
                expectedOperation,
                StateGuard.Type.USER,
                expectedId
            )
        );

        // Then
        then(userRepository)
            .should()
            .findById(expectedId);

        assertThat(result)
            .isNull();
    }
}
