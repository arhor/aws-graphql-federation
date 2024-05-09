package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserService
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.UUID

class UserEventSqsListenerTest {

    private val userService = mockk<UserService>()
    private val userEventSqsListener = UserEventSqsListener(userService)

    @AfterEach
    fun tearDown() {
        confirmVerified(userService)
    }

    @Test
    fun `should call createInternalUserRepresentation on UserEvent#Created`() {
        // Given
        val event = UserEvent.Created(id = UUID.randomUUID())

        every { userService.createInternalUserRepresentation(any()) } just runs

        // When
        userEventSqsListener.handleUserCreatedEvent(event)

        // Then
        verify(exactly = 1) { userService.createInternalUserRepresentation(event.ids) }
    }

    @Test
    fun `should call deleteInternalUserRepresentation on UserEvent#Deleted`() {
        // Given
        val event = UserEvent.Deleted(id = UUID.randomUUID())

        every { userService.deleteInternalUserRepresentation(any()) } just runs

        // When
        userEventSqsListener.handleUserDeletedEvent(event)

        // Then
        verify(exactly = 1) { userService.deleteInternalUserRepresentation(event.ids) }
    }
}
