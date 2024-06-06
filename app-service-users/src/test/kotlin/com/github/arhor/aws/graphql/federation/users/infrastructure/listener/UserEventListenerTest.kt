package com.github.arhor.aws.graphql.federation.users.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import com.github.arhor.aws.graphql.federation.users.service.OutboxMessageService
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class UserEventListenerTest {

    private val outboxMessageService = mockk<OutboxMessageService>()
    private val userEventListener = UserEventListener(outboxMessageService)


    @MethodSource
    @ParameterizedTest
    fun `should call OutboxMessageService#storeAsOutboxMessage on any UserEvent`(
        // Given
        event: UserEvent,
    ) {
        every { outboxMessageService.storeAsOutboxMessage(any()) } just runs

        // When
        userEventListener.onUserEvent(event)

        // Then
        verify(exactly = 1) { outboxMessageService.storeAsOutboxMessage(event) }

        confirmVerified(outboxMessageService)
    }

    companion object {
        private val USER_ID = ZERO_UUID_VAL

        @JvmStatic
        fun `should call OutboxMessageService#storeAsOutboxMessage on any UserEvent`(): Stream<Arguments> = Stream.of(
            Arguments.of(UserEvent.Created(id = USER_ID)),
            Arguments.of(UserEvent.Deleted(id = USER_ID)),
        )
    }
}
