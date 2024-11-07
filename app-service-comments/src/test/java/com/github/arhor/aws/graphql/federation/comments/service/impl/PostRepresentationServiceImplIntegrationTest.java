package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import com.github.arhor.aws.graphql.federation.starter.testing.RedisCacheTest;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.assertj.core.api.Assertions.from;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.then;

@Tag("integration")
@EnableCaching
@RedisCacheTest(classes = PostRepresentationServiceImpl.class)
@Testcontainers(disabledWithoutDocker = true)
class PostRepresentationServiceImplIntegrationTest {

    private static final UUID POST_ID = ConstantsKt.getTEST_1_UUID_VAL();
    private static final UUID USER_ID = ConstantsKt.getTEST_2_UUID_VAL();
    private static final UUID IDEMPOTENCY_KEY = ConstantsKt.getTEST_3_UUID_VAL();

    @Container
    private final static RedisContainer REDIS = new RedisContainer(DockerImageName.parse("redis:7-alpine"));

    @Captor
    private ArgumentCaptor<PostRepresentation> postRepresentationCaptor;

    @MockBean
    private PostRepresentationRepository postRepresentationRepository;

    @MockBean
    private StateGuard stateGuard;

    @Autowired
    private PostRepresentationService postRepresentationService;

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getRedisHost);
        registry.add("spring.data.redis.port", REDIS::getRedisPort);
    }

    @Test
    void should_call_postRepresentationRepository_save_only_once_with_the_same_idempotencyKey() {
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
    void should_call_postRepresentationRepository_deleteById_only_once_with_the_same_idempotencyKey() {
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
