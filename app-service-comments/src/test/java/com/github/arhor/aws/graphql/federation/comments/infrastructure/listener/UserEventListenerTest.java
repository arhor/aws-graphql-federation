package com.github.arhor.aws.graphql.federation.comments.infrastructure.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
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

@ContextConfiguration(classes = {UserEventListener.class})
class UserEventListenerTest extends EventListenerTestBase {

    private static final String USER_CREATED_TEST_QUEUE = "user-created-test-queue";
    private static final String USER_DELETED_TEST_QUEUE = "user-deleted-test-queue";

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TRACE_ID = UUID.randomUUID();
    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();

    @MockBean
    private UserRepresentationService userRepresentationService;

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("app-props.aws.sqs.user-created-events", () -> USER_CREATED_TEST_QUEUE);
        registry.add("app-props.aws.sqs.user-deleted-events", () -> USER_DELETED_TEST_QUEUE);
    }

    @BeforeAll
    static void createdTestQueues() throws IOException, InterruptedException {
        createdQueue(USER_CREATED_TEST_QUEUE);
        createdQueue(USER_DELETED_TEST_QUEUE);
    }

    @Test
    void should_call_createUserRepresentation_method_on_user_created_event() {
        // Given
        final var event = new UserEvent.Created(USER_ID);

        // When
        sqsTemplate.send(
            USER_CREATED_TEST_QUEUE,
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
                then(userRepresentationService)
                    .should()
                    .createUserRepresentation(event.getId(), IDEMPOTENCY_KEY);

                then(userRepresentationService)
                    .shouldHaveNoMoreInteractions();
            });
    }

    @Test
    void should_call_deleteUserRepresentation_method_on_user_deleted_event() {
        // Given
        final var event = new UserEvent.Deleted(USER_ID);

        // When
        sqsTemplate.send(
            USER_DELETED_TEST_QUEUE,
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
                then(userRepresentationService)
                    .should()
                    .deleteUserRepresentation(event.getId(), IDEMPOTENCY_KEY);

                then(userRepresentationService)
                    .shouldHaveNoMoreInteractions();
            });
    }
}
