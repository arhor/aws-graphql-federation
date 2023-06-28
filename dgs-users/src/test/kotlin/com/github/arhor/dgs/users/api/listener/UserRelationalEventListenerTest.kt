@file:Suppress("ClassName")

package com.github.arhor.dgs.users.api.listener

import com.github.arhor.dgs.users.api.listener.UserRelationalEventListener.UserStateChange
import com.github.arhor.dgs.users.config.props.AppProps
import com.github.arhor.dgs.users.data.entity.UserEntity
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.data.relational.core.conversion.AggregateChange
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent
import org.springframework.data.relational.core.mapping.event.Identifier

internal class UserRelationalEventListenerTest {

    private val mockSnsOperations = mockk<SnsOperations>()
    private val mockAppProps = mockk<AppProps> {
        every { aws.sns.userStateChanges } returns TEST_USER_STATE_CHANGES
    }

    private val listener = UserRelationalEventListener(
        mockSnsOperations,
        mockAppProps,
    )

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
        val expectedPayload = UserStateChange.Updated(STUB_USER_ID)
        val expectedSnsName = TEST_USER_STATE_CHANGES

        // When
        listener.onApplicationEvent(relationalEvent)

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
        val expectedPayload = UserStateChange.Deleted(STUB_USER_ID)
        val expectedSnsName = TEST_USER_STATE_CHANGES

        // When
        listener.onApplicationEvent(relationalEvent)

        // Then
        assertThat(snsTopicName)
            .returns(true, from { it.isCaptured })
            .returns(expectedSnsName, from { it.captured })

        assertThat(notification)
            .returns(true, from { it.isCaptured })
            .returns(expectedPayload, from { it.captured.payload })
    }

    companion object {
        private const val TEST_USER_STATE_CHANGES = "test-user-state-changes"
        private const val STUB_USER_ID = -1L
    }
}
