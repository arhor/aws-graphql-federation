package com.github.arhor.aws.graphql.federation.comments.infrastructure.listener;

import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.then;

@ContextConfiguration(classes = {PostEventListener.class})
class PostEventListenerTest extends EventListenerTestBase {

    private static final String POST_CREATED_TEST_QUEUE = "sync-comments-on-post-created-event-test-queue";
    private static final String POST_DELETED_TEST_QUEUE = "sync-comments-on-post-deleted-event-test-queue";

    @MockBean
    private PostRepresentationService postRepresentationService;

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("app-props.aws.sqs.sync-comments-on-post-created-event", () -> POST_CREATED_TEST_QUEUE);
        registry.add("app-props.aws.sqs.sync-comments-on-post-deleted-event", () -> POST_DELETED_TEST_QUEUE);
    }

    @BeforeAll
    static void createdTestQueues() throws IOException, InterruptedException {
        createdQueue(POST_CREATED_TEST_QUEUE);
        createdQueue(POST_DELETED_TEST_QUEUE);
    }

    @Test
    @DisplayName("should call createPostRepresentation method on post created event")
    void should_call_createPostRepresentation_method_on_post_created_event() {
        // Given
        final var event = new PostEvent.Created(POST_ID, USER_ID);
        final var message = new GenericMessage<>(event, MESSAGE_HEADERS);

        // When
        sqsTemplate.send(POST_CREATED_TEST_QUEUE, message);

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(postRepresentationService)
                    .should()
                    .createPostRepresentation(event.getId(), event.getUserId(), IDEMPOTENCY_KEY);

                then(postRepresentationService)
                    .shouldHaveNoMoreInteractions();
            });
    }

    @Test
    @DisplayName("should call deletePostRepresentation method on post deleted event")
    void should_call_deletePostRepresentation_method_on_post_deleted_event() {
        // Given
        final var event = new PostEvent.Deleted(POST_ID);
        final var message = new GenericMessage<>(event, MESSAGE_HEADERS);

        // When
        sqsTemplate.send(POST_DELETED_TEST_QUEUE, message);

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(postRepresentationService)
                    .should()
                    .deletePostRepresentation(event.getId(), IDEMPOTENCY_KEY);

                then(postRepresentationService)
                    .shouldHaveNoMoreInteractions();
            });
    }
}
