package com.github.arhor.aws.graphql.federation.comments.infrastructure.listener;

import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.common.event.UserEvent;
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

@ContextConfiguration(classes = {UserEventListener.class})
class UserEventListenerTest extends EventListenerTestBase {

    private static final String USER_CREATED_TEST_QUEUE = "sync-comments-on-user-created-event-test-queue";
    private static final String USER_DELETED_TEST_QUEUE = "sync-comments-on-user-deleted-event-test-queue";

    @MockBean
    private UserRepresentationService userRepresentationService;

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("app-props.aws.sqs.sync-comments-on-user-created-event", () -> USER_CREATED_TEST_QUEUE);
        registry.add("app-props.aws.sqs.sync-comments-on-user-deleted-event", () -> USER_DELETED_TEST_QUEUE);
    }

    @BeforeAll
    static void createdTestQueues() throws IOException, InterruptedException {
        createdQueue(USER_CREATED_TEST_QUEUE);
        createdQueue(USER_DELETED_TEST_QUEUE);
    }

    @Test
    @DisplayName("should call createUserRepresentation method on user created event")
    void should_call_createUserRepresentation_method_on_user_created_event() {
        // Given
        final var event = new UserEvent.Created(USER_ID);
        final var message = new GenericMessage<>(event, MESSAGE_HEADERS);

        // When
        sqsTemplate.send(USER_CREATED_TEST_QUEUE, message);

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
    @DisplayName("should call deleteUserRepresentation method on user deleted event")
    void should_call_deleteUserRepresentation_method_on_user_deleted_event() {
        // Given
        final var event = new UserEvent.Deleted(USER_ID);
        final var message = new GenericMessage<>(event, MESSAGE_HEADERS);

        // When
        sqsTemplate.send(USER_DELETED_TEST_QUEUE, message);

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
