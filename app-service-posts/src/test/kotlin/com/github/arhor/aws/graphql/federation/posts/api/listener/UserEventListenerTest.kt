package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.constants.ATTR_TRACE_ID
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
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
        val message = GenericMessage(event, MESSAGE_HEADERS)

        every { userRepresentationService.createUserRepresentation(any()) } just runs

        // When
        sqsTemplate.send(USER_CREATED_TEST_QUEUE, message)

        // Then
        verify(exactly = 1, timeout = 5.seconds.inWholeMilliseconds) {
            userRepresentationService.createUserRepresentation(event.id)
        }
    }

    @Test
    fun `should call deleteUserRepresentation on UserEvent#Deleted`() {
        // Given
        val event = UserEvent.Deleted(id = USER_ID)
        val message = GenericMessage(event, MESSAGE_HEADERS)

        every { userRepresentationService.deleteUserRepresentation(any()) } just runs

        // When
        sqsTemplate.send(USER_DELETED_TEST_QUEUE, message)

        // Then
        verify(exactly = 1, timeout = 5.seconds.inWholeMilliseconds) {
            userRepresentationService.deleteUserRepresentation(event.id)
        }
    }

    companion object {
        private const val USER_CREATED_TEST_QUEUE = "sync-posts-on-user-created-event-test"
        private const val USER_DELETED_TEST_QUEUE = "sync-posts-on-user-deleted-event-test"

        private val USER_ID = ZERO_UUID_VAL
        private val TRACE_ID = TEST_1_UUID_VAL
        private val MESSAGE_HEADERS = mapOf(ATTR_TRACE_ID to TRACE_ID)

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) = with(registry) {
            add("app-props.events.source.sync-posts-on-user-created-event") { USER_CREATED_TEST_QUEUE }
            add("app-props.events.source.sync-posts-on-user-deleted-event") { USER_DELETED_TEST_QUEUE }
        }

        @JvmStatic
        @BeforeAll
        fun createdTestQueues() {
            createdQueue(USER_CREATED_TEST_QUEUE)
            createdQueue(USER_DELETED_TEST_QUEUE)
        }
    }
}
