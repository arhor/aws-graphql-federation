@file:Suppress("ClassName")

package com.github.arhor.dgs.users.data.listener

import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.data.listener.UserStateChangedEventListener.USER_DELETED_EVENTS_PROP
import com.github.arhor.dgs.users.data.listener.UserStateChangedEventListener.USER_UPDATED_EVENTS_PROP
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.type
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.core.env.MissingRequiredPropertiesException
import org.springframework.data.relational.core.conversion.AggregateChange
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent
import org.springframework.data.relational.core.mapping.event.Identifier
import java.util.stream.Stream

internal class UserStateChangedEventListenerTest {

    private val mockSnsOperations = mockk<SnsOperations>()

    private val listener = UserStateChangedEventListener(
        mockSnsOperations,
        TEST_USER_UPDATED_EVENTS,
        TEST_USER_DELETED_EVENTS,
    )

    @MethodSource
    @ParameterizedTest
    fun `constructor should throw MissingRequiredPropertiesException`(
        // Given
        userUpdatedEventsTopic: String?,
        userDeletedEventsTopic: String?,
        expectedMissingProperties: Set<String>,
    ) {
        // When
        val result = catchThrowable {
            UserStateChangedEventListener(
                mockSnsOperations,
                userUpdatedEventsTopic,
                userDeletedEventsTopic,
            )
        }

        // Then
        assertThat(result)
            .asInstanceOf(type(MissingRequiredPropertiesException::class.java))
            .returns(expectedMissingProperties) { it.missingRequiredProperties }
    }

    @Test
    fun `onAfterSave should send SNS notification with expected topic name and payload`() {
        // Given
        val entity = mockk<UserEntity>()
        val change = mockk<AggregateChange<UserEntity>>()

        val snsTopicName = slot<String>()
        val notification = slot<SnsNotification<*>>()

        every { entity.id } returns STUB_USER_ID
        every { mockSnsOperations.sendNotification(capture(snsTopicName), capture(notification)) } just runs

        val relationalEvent = AfterSaveEvent(entity, change)
        val expectedPayload = UserStateChange.Updated(relationalEvent)
        val expectedSnsName = TEST_USER_UPDATED_EVENTS

        // When
        listener.onAfterSave(relationalEvent)

        // Then
        assertThat(snsTopicName)
            .returns(true, from { it.isCaptured })
            .returns(expectedSnsName, from { it.captured })

        assertThat(notification)
            .returns(true, from { it.isCaptured })
            .returns(expectedPayload, from { it.captured.payload })
    }

    @Test
    fun `onAfterDelete should send SNS notification with expected topic name and payload`() {
        // Given
        val id = mockk<Identifier>()
        val entity = mockk<UserEntity>()
        val change = mockk<AggregateChange<UserEntity>>()

        val snsTopicName = slot<String>()
        val notification = slot<SnsNotification<*>>()

        every { id.value } returns STUB_USER_ID
        every { change.entityType } returns UserEntity::class.java
        every { mockSnsOperations.sendNotification(capture(snsTopicName), capture(notification)) } just runs

        val relationalEvent = AfterDeleteEvent(id, entity, change)
        val expectedPayload = UserStateChange.Deleted(relationalEvent)
        val expectedSnsName = TEST_USER_DELETED_EVENTS

        // When
        listener.onAfterDelete(relationalEvent)

        // Then
        assertThat(snsTopicName)
            .returns(true, from { it.isCaptured })
            .returns(expectedSnsName, from { it.captured })

        assertThat(notification)
            .returns(true, from { it.isCaptured })
            .returns(expectedPayload, from { it.captured.payload })
    }

    companion object {
        private const val TEST_USER_UPDATED_EVENTS = "test-user-updated-events"
        private const val TEST_USER_DELETED_EVENTS = "test-user-deleted-events"

        private const val STUB_USER_ID = -1L

        @JvmStatic
        fun `constructor should throw MissingRequiredPropertiesException`(): Stream<Arguments> =
            Stream.of(
                arguments(null, null, setOf(USER_UPDATED_EVENTS_PROP, USER_DELETED_EVENTS_PROP)),
                arguments(TEST_USER_UPDATED_EVENTS, null, setOf(USER_DELETED_EVENTS_PROP)),
                arguments(null, TEST_USER_DELETED_EVENTS, setOf(USER_UPDATED_EVENTS_PROP)),
            )
    }
}
