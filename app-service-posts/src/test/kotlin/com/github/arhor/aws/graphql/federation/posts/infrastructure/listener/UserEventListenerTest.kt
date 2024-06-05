package com.github.arhor.aws.graphql.federation.posts.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.starter.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.TRACING_ID_KEY
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@ContextConfiguration(classes = [UserEventListener::class])
class UserEventListenerTest : SqsListenerTestBase() {

    @MockkBean
    private lateinit var userRepresentationService: UserRepresentationService

    @AfterEach
    fun tearDown() {
        confirmVerified(userRepresentationService)
    }

    @Test
    fun `should call createUserRepresentation on UserEvent#Created`() {
        // Given
        val event = UserEvent.Created(id = USER_ID)

        every { userRepresentationService.createUserRepresentation(any(), any()) } just runs

        // When
        sqsTemplate.send(
            USER_CREATED_TEST_QUEUE,
            GenericMessage(
                event,
                mapOf(
                    TRACING_ID_KEY to TRACE_ID,
                    IDEMPOTENT_KEY to IDEMPOTENCY_KEY,
                )
            )
        )

        // Then
        verify(exactly = 1, timeout = 5.seconds.inWholeMilliseconds) {
            userRepresentationService.createUserRepresentation(event.id, IDEMPOTENCY_KEY)
        }
    }

    @Test
    fun `should call deleteUserRepresentation on UserEvent#Deleted`() {
        // Given
        val event = UserEvent.Deleted(id = USER_ID)

        every { userRepresentationService.deleteUserRepresentation(any(), any()) } just runs

        // When
        sqsTemplate.send(
            USER_DELETED_TEST_QUEUE,
            GenericMessage(
                event,
                mapOf(
                    TRACING_ID_KEY to TRACE_ID,
                    IDEMPOTENT_KEY to IDEMPOTENCY_KEY,
                )
            )
        )

        // Then
        verify(exactly = 1, timeout = 5.seconds.inWholeMilliseconds) {
            userRepresentationService.deleteUserRepresentation(event.id, IDEMPOTENCY_KEY)
        }
    }

    companion object {
        private const val USER_CREATED_TEST_QUEUE = "user-created-test-queue"
        private const val USER_DELETED_TEST_QUEUE = "user-deleted-test-queue"

        private val USER_ID = UUID.randomUUID()
        private val TRACE_ID = UUID.randomUUID()
        private val IDEMPOTENCY_KEY = UUID.randomUUID()

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) = with(registry) {
            add("app-props.aws.sqs.user-created-events") { USER_CREATED_TEST_QUEUE }
            add("app-props.aws.sqs.user-deleted-events") { USER_DELETED_TEST_QUEUE }
        }

        @JvmStatic
        @BeforeAll
        fun createdTestQueues() {
            createdQueue(USER_CREATED_TEST_QUEUE)
            createdQueue(USER_DELETED_TEST_QUEUE)
        }
    }
}
