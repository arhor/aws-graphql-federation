package com.github.arhor.aws.graphql.federation.comments.infrastructure.listener;

import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.arhor.aws.graphql.federation.tracing.AttributesKt.IDEMPOTENT_KEY;
import static com.github.arhor.aws.graphql.federation.tracing.AttributesKt.TRACING_ID_KEY;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.then;

@ContextConfiguration(classes = {PostEventListener.class})
class PostEventListenerTest extends EventListenerTestBase {

    private static final String POST_CREATED_TEST_QUEUE = "post-created-test-queue";
    private static final String POST_DELETED_TEST_QUEUE = "post-deleted-test-queue";

    private static final UUID POST_ID = UUID.randomUUID();
    private static final UUID TRACE_ID = UUID.randomUUID();
    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();

    @MockBean
    private PostRepresentationService postRepresentationService;

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("app-props.aws.sqs.post-created-events", () -> POST_CREATED_TEST_QUEUE);
        registry.add("app-props.aws.sqs.post-deleted-events", () -> POST_DELETED_TEST_QUEUE);
    }

    @BeforeAll
    static void createdTestQueues() throws IOException, InterruptedException {
        createdQueue(POST_CREATED_TEST_QUEUE);
        createdQueue(POST_DELETED_TEST_QUEUE);
    }

    @Test
    void should_call_createPostRepresentation_method_on_post_created_event() {
        // Given
        final var event = new PostEvent.Created(POST_ID);

        // When
        sqsTemplate.send(
            POST_CREATED_TEST_QUEUE,
            new GenericMessage<>(
                event,
                Map.of(
                    TRACING_ID_KEY, TRACE_ID,
                    IDEMPOTENT_KEY, IDEMPOTENCY_KEY
                )
            )
        );

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(postRepresentationService)
                    .should()
                    .createPostRepresentation(event.getId(), IDEMPOTENCY_KEY);

                then(postRepresentationService)
                    .shouldHaveNoMoreInteractions();
            });
    }

    @Test
    void should_call_deletePostRepresentation_method_on_post_deleted_event() {
        // Given
        final var event = new PostEvent.Deleted(POST_ID);

        // When
        sqsTemplate.send(
            POST_DELETED_TEST_QUEUE,
            new GenericMessage<>(
                event,
                Map.of(
                    TRACING_ID_KEY, TRACE_ID,
                    IDEMPOTENT_KEY, IDEMPOTENCY_KEY
                )
            )
        );

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
