package com.github.arhor.aws.graphql.federation.comments.api.listener;

import com.github.arhor.aws.graphql.federation.comments.service.PostService;
import com.github.arhor.aws.graphql.federation.common.event.PostEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.then;

@ContextConfiguration(classes = {PostEventListener.class})
class PostEventListenerTest extends EventListenerTestBase {

    private static final String POST_CREATED_TEST_QUEUE = "post-created-test-queue";
    private static final String POST_DELETED_TEST_QUEUE = "post-deleted-test-queue";

    @MockBean
    private PostService postService;

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
    void should_call_createInternalPostRepresentation_method_on_post_created_event() {
        // Given
        final var event = new PostEvent.Created(UUID.randomUUID());

        // When
        sqsTemplate.send(POST_CREATED_TEST_QUEUE, event);

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(postService)
                    .should()
                    .createInternalPostRepresentation(event.getIds());

                then(postService)
                    .shouldHaveNoMoreInteractions();
            });
    }

    @Test
    void should_call_deleteInternalPostRepresentation_method_on_post_deleted_event() {
        // Given
        final var event = new PostEvent.Deleted(UUID.randomUUID());

        // When
        sqsTemplate.send(POST_DELETED_TEST_QUEUE, event);

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                then(postService)
                    .should()
                    .deleteInternalPostRepresentation(event.getIds());

                then(postService)
                    .shouldHaveNoMoreInteractions();
            });
    }
}
