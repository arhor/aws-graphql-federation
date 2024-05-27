package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
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
import java.util.Set;
import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.comments.util.Caches.IDEMPOTENT_ID_SET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRepresentationServiceImplTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();

    private final Cache cache = new ConcurrentMapCache(IDEMPOTENT_ID_SET.name());
    private final CacheManager cacheManager = mock();
    private final UserRepresentationRepository userRepresentationRepository = mock();

    private UserRepresentationServiceImpl userService;

    @BeforeEach
    void setUp() {
        when(cacheManager.getCache(IDEMPOTENT_ID_SET.name()))
            .thenReturn(cache);

        userService = new UserRepresentationServiceImpl(cacheManager, userRepresentationRepository);
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
                    .commentsDisabled(false)
                    .build();

            final var expectedResult = Map.of(
                USER_ID,
                User.newBuilder()
                    .id(USER_ID)
                    .commentsDisabled(userRepresentation.commentsDisabled())
                    .build()
            );
            final var expectedUserIds = Set.of(USER_ID);

            when(userRepresentationRepository.findAllById(any()))
                .thenReturn(List.of(userRepresentation));

            // When
            final var result = userService.findUsersRepresentationsInBatch(expectedUserIds);

            // Then
            then(userRepresentationRepository)
                .should()
                .findAllById(expectedUserIds);

            then(userRepresentationRepository)
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

            when(userRepresentationRepository.findAllById(any()))
                .thenReturn(Collections.emptyList());

            // When
            final var result = userService.findUsersRepresentationsInBatch(expectedUserIds);

            // Then
            then(userRepresentationRepository)
                .should()
                .findAllById(expectedUserIds);

            then(userRepresentationRepository)
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
                    .commentsDisabled(false)
                    .shouldBePersisted(true)
                    .build();

            // When
            for (int i = 0; i < 3; i++) {
                userService.createUserRepresentation(expectedUserRepresentation.id(), IDEMPOTENCY_KEY);
            }

            // Then
            then(userRepresentationRepository)
                .should()
                .save(expectedUserRepresentation);

            then(userRepresentationRepository)
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
            then(userRepresentationRepository)
                .should()
                .deleteById(USER_ID);

            then(userRepresentationRepository)
                .shouldHaveNoMoreInteractions();
        }
    }
}
