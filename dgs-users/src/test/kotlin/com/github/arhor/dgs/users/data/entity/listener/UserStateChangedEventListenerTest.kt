@file:Suppress("ClassName")

package com.github.arhor.dgs.users.data.entity.listener

import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.data.entity.listener.UserStateChangedEventListener.USER_DELETED_EVENTS_PROP
import com.github.arhor.dgs.users.data.entity.listener.UserStateChangedEventListener.USER_UPDATED_EVENTS_PROP
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.relational.core.conversion.AggregateChange
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

private const val TEST_USER_UPDATED_EVENTS = "test-user-updated-events"
private const val TEST_USER_DELETED_EVENTS = "test-user-deleted-events"

@SpringJUnitConfig(
    classes = [
        UserStateChangedEventListener::class,
    ]
)
@TestPropertySource(
    properties = [
        "$USER_UPDATED_EVENTS_PROP=$TEST_USER_UPDATED_EVENTS",
        "$USER_DELETED_EVENTS_PROP=$TEST_USER_DELETED_EVENTS",
    ]
)
internal class UserStateChangedEventListenerTest {

    @Autowired
    private lateinit var listener: UserStateChangedEventListener

    @MockkBean
    private lateinit var snsOperations: SnsOperations

    @Nested
    inner class onApplicationEvent {
        @Test
        fun `should send update notification to SNS using correct event type and destination`() {
            // Given
            val entity = mockk<UserEntity>()
            val change = mockk<AggregateChange<UserEntity>>()

            // When
            listener.onApplicationEvent(AfterSaveEvent(entity, change))

            // Then

        }

        @Test
        fun `should send delete notification to SNS using correct event type and destination`() {
            // Given
            val event = AfterDeleteEvent(mockk(), mockk<UserEntity>(), mockk())

            // When
            listener.onApplicationEvent(event)

            // Then

        }
    }
}
