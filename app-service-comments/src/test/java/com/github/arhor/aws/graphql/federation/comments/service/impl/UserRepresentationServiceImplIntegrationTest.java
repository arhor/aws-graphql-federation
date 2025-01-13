package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static org.assertj.core.api.Assertions.from;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.then;

@EnableCaching
@SpringJUnitConfig(UserRepresentationServiceImpl.class)
@AutoConfigureCache(cacheProvider = CacheType.CAFFEINE)
class UserRepresentationServiceImplIntegrationTest {

    private static final UUID USER_ID = ConstantsKt.getTEST_1_UUID_VAL();

    @Captor
    private ArgumentCaptor<UserRepresentation> userRepresentationCaptor;

    @MockBean
    private UserRepresentationRepository userRepresentationRepository;

    @Autowired
    private UserRepresentationService userRepresentationService;

    @Test
    void should_call_userRepository_save_only_once_with_the_same_user_id() {
        // Given
        final var numberOfCalls = 3;

        // When
        for (int i = 1; i <= numberOfCalls; i++) {
            userRepresentationService.createUserRepresentation(USER_ID);
        }

        // Then
        then(userRepresentationRepository)
            .should()
            .save(userRepresentationCaptor.capture());

        then(userRepresentationRepository)
            .shouldHaveNoMoreInteractions();

        assertThat(userRepresentationCaptor.getValue())
            .returns(USER_ID, from(UserRepresentation::id))
            .returns(true, from(UserRepresentation::isNew))
            .returns(true, from(UserRepresentation::shouldBePersisted));
    }

    @Test
    void should_call_userRepository_deleteById_only_once_with_the_same_user_id() {
        // Given
        final var numberOfCalls = 3;

        // When
        for (int i = 0; i < numberOfCalls; i++) {
            userRepresentationService.deleteUserRepresentation(USER_ID);
        }

        // Then
        then(userRepresentationRepository)
            .should()
            .deleteById(USER_ID);

        then(userRepresentationRepository)
            .shouldHaveNoMoreInteractions();
    }
}
