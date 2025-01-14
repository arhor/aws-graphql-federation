package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.Tag;
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

@Tag("integration")
@EnableCaching
@SpringJUnitConfig(PostRepresentationServiceImpl.class)
@AutoConfigureCache(cacheProvider = CacheType.CAFFEINE)
class PostRepresentationServiceImplIntegrationTest {

    private static final UUID POST_ID = ConstantsKt.getTEST_1_UUID_VAL();
    private static final UUID USER_ID = ConstantsKt.getTEST_2_UUID_VAL();
    private static final UUID IDEMPOTENCY_KEY = ConstantsKt.getTEST_3_UUID_VAL();

    @Captor
    private ArgumentCaptor<PostRepresentation> postRepresentationCaptor;

    @MockBean
    private PostRepresentationRepository postRepresentationRepository;

    @MockBean
    private StateGuard stateGuard;

    @Autowired
    private PostRepresentationService postRepresentationService;

    @Test
    void should_call_postRepresentationRepository_save_only_once_with_the_same_post_and_user_ids() {
        // Given
        final var numberOfCalls = 3;

        // When
        for (int i = 1; i <= numberOfCalls; i++) {
            postRepresentationService.createPostRepresentation(POST_ID, USER_ID, IDEMPOTENCY_KEY);
        }

        // Then
        then(postRepresentationRepository)
            .should()
            .save(postRepresentationCaptor.capture());

        then(postRepresentationRepository)
            .shouldHaveNoMoreInteractions();

        assertThat(postRepresentationCaptor.getValue())
            .returns(POST_ID, from(PostRepresentation::id))
            .returns(USER_ID, from(PostRepresentation::userId))
            .returns(true, from(PostRepresentation::isNew))
            .returns(true, from(PostRepresentation::shouldBePersisted));
    }

    @Test
    void should_call_postRepresentationRepository_deleteById_only_once_with_the_same_post_id() {
        // Given
        final var numberOfCalls = 3;

        // When
        for (int i = 0; i < numberOfCalls; i++) {
            postRepresentationService.deletePostRepresentation(POST_ID, IDEMPOTENCY_KEY);
        }

        // Then
        then(postRepresentationRepository)
            .should()
            .deleteById(POST_ID);

        then(postRepresentationRepository)
            .shouldHaveNoMoreInteractions();
    }
}
