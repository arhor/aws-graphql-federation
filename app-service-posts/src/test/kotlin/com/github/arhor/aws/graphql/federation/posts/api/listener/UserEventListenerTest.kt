package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.DomainEvent.Companion.HEADER_IDEMPOTENCY_KEY
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserService
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
class UserEventListenerTest : EventListenerTestBase() {

    @MockkBean
    private lateinit var userService: UserService

    @AfterEach
    fun tearDown() {
        confirmVerified(userService)
    }

    @Test
    fun `should call createInternalUserRepresentation on UserEvent#Created`() {
        // Given
        val idempotencyKey = UUID.randomUUID()
        val event = UserEvent.Created(id = UUID.randomUUID())

        every { userService.createInternalUserRepresentation(any(), any()) } just runs

        // When
        sqsTemplate.send(
            USER_CREATED_TEST_QUEUE,
            GenericMessage(
                event,
                mapOf(HEADER_IDEMPOTENCY_KEY to idempotencyKey)
            )
        )

        // Then
        verify(exactly = 1, timeout = 5.seconds.inWholeMilliseconds) {
            userService.createInternalUserRepresentation(event.id, idempotencyKey)
        }
    }

    @Test
    fun `should call deleteInternalUserRepresentation on UserEvent#Deleted`() {
        // Given
        val idempotencyKey = UUID.randomUUID()
        val event = UserEvent.Deleted(id = UUID.randomUUID())

        every { userService.deleteInternalUserRepresentation(any(), any()) } just runs

        // When
        sqsTemplate.send(
            USER_DELETED_TEST_QUEUE,
            GenericMessage(
                event,
                mapOf(HEADER_IDEMPOTENCY_KEY to idempotencyKey)
            )
        )

        // Then
        verify(exactly = 1, timeout = 5.seconds.inWholeMilliseconds) {
            userService.deleteInternalUserRepresentation(event.id, idempotencyKey)
        }
    }

    companion object {
        private const val USER_CREATED_TEST_QUEUE = "user-created-test-queue"
        private const val USER_DELETED_TEST_QUEUE = "user-deleted-test-queue"

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
