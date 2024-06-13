package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments.Feature;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.common.exception.EntityConditionException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.comments.util.Caches.IDEMPOTENT_ID_SET;
import static com.github.arhor.aws.graphql.federation.starter.testing.MockitoExtKt.withFirstArg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.from;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class UserRepresentationServiceImplTest {

    private static final UUID USER_ID = ConstantsKt.getTEST_1_UUID_VAL();
    private static final UUID IDEMPOTENCY_KEY = ConstantsKt.getTEST_2_UUID_VAL();

    private final Cache cache = new ConcurrentMapCache(IDEMPOTENT_ID_SET.name());
    private final CacheManager cacheManager = mock();
    private final UserRepresentationRepository userRepository = mock();

    private UserRepresentationServiceImpl userService;

    @BeforeEach
    void setUp() {
        given(cacheManager.getCache(IDEMPOTENT_ID_SET.name()))
            .willReturn(cache);

        userService = new UserRepresentationServiceImpl(cacheManager, userRepository);
        userService.initialize();
    }


    @Nested
    @DisplayName("UserService :: findUsersRepresentationsInBatch")
    class FindUserRepresentationTest {
        @Test
        void should_return_expected_user_when_it_exists_by_id() {
            // Given
            final var userRepresentation =
                UserRepresentation.builder()
                    .id(USER_ID)
                    .build();

            final var expectedResult = Map.of(
                USER_ID,
                User.newBuilder()
                    .id(USER_ID)
                    .commentsDisabled(false)
                    .build()
            );
            final var expectedUserIds = Set.of(USER_ID);

            given(userRepository.findAllById(any()))
                .willReturn(List.of(userRepresentation));

            // When
            final var result = userService.findUsersRepresentationsInBatch(expectedUserIds);

            // Then
            then(userRepository)
                .should()
                .findAllById(expectedUserIds);

            then(userRepository)
                .shouldHaveNoMoreInteractions();

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult);
        }

        @Test
        void should_return_user_with_commentsOperable_false_when_user_does_not_exist_by_id() {
            // Given
            final var expectedResult = Map.of(
                USER_ID,
                User.newBuilder()
                    .id(USER_ID)
                    .build()
            );
            final var expectedUserIds = Set.of(USER_ID);

            given(userRepository.findAllById(any()))
                .willReturn(Collections.emptyList());

            // When
            final var result = userService.findUsersRepresentationsInBatch(expectedUserIds);

            // Then
            then(userRepository)
                .should()
                .findAllById(expectedUserIds);

            then(userRepository)
                .shouldHaveNoMoreInteractions();

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("UserService :: createUserRepresentation")
    class CreateUserRepresentationTest {
        @Test
        void should_call_userRepository_save_only_once_with_the_same_idempotencyKey() {
            // Given
            final var expectedUserRepresentation =
                UserRepresentation.builder()
                    .id(USER_ID)
                    .shouldBePersisted(true)
                    .build();

            // When
            for (int i = 0; i < 3; i++) {
                userService.createUserRepresentation(expectedUserRepresentation.id(), IDEMPOTENCY_KEY);
            }

            // Then
            then(userRepository)
                .should()
                .save(expectedUserRepresentation);

            then(userRepository)
                .shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("UserService :: deleteUserRepresentation")
    class DeleteUserRepresentationTest {
        @Test
        void should_call_userRepository_deleteById_only_once_with_the_same_idempotencyKey() {
            // Given
            final var numberOfCalls = 3;

            // When
            for (int i = 0; i < numberOfCalls; i++) {
                userService.deleteUserRepresentation(USER_ID, IDEMPOTENCY_KEY);
            }

            // Then
            then(userRepository)
                .should()
                .deleteById(USER_ID);

            then(userRepository)
                .shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("UserService :: toggleUserComments")
    class SwitchUserCommentsTest {
        @Test
        void should_disable_comments_for_a_given_post_when_they_were_enabled() {
            // Given
            final var user =
                UserRepresentation.builder()
                    .id(USER_ID)
                    .build();

            given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

            given(userRepository.save(any()))
                .willAnswer(withFirstArg());

            // When
            final var result = userService.toggleUserComments(USER_ID);

            // Then
            then(userRepository)
                .should()
                .findById(USER_ID);

            then(userRepository)
                .should()
                .save(user.toBuilder().features(user.features().plus(Feature.COMMENTS_DISABLED)).build());

            assertThat(result)
                .isFalse();
        }

        @Test
        void should_enable_comments_for_a_given_post_when_they_were_disabled() {
            // Given
            final var user =
                UserRepresentation.builder()
                    .id(USER_ID)
                    .features(Features.of(Feature.COMMENTS_DISABLED))
                    .build();

            given(userRepository.findById(any()))
                .willReturn(Optional.of(user));

            given(userRepository.save(any()))
                .willAnswer(withFirstArg());

            // When
            final var result = userService.toggleUserComments(USER_ID);

            // Then
            then(userRepository)
                .should()
                .findById(USER_ID);

            then(userRepository)
                .should()
                .save(user.toBuilder().features(user.features().minus(Feature.COMMENTS_DISABLED)).build());

            assertThat(result)
                .isTrue();
        }

        @Test
        void should_throw_EntityNotFoundException_when_there_is_no_user_found_by_the_input_id() {
            // Given
            given(userRepository.findById(any()))
                .willReturn(Optional.empty());

            // When
            final var result = catchException(() -> userService.toggleUserComments(USER_ID));

            // Then
            then(userRepository)
                .should()
                .findById(USER_ID);

            assertThat(result)
                .asInstanceOf(type(EntityNotFoundException.class))
                .returns(USER.TYPE_NAME, from(EntityConditionException::getEntity))
                .returns(USER.Id + " = " + USER_ID, from(EntityConditionException::getCondition))
                .returns(Operation.UPDATE, from(EntityConditionException::getOperation));
        }
    }
}
