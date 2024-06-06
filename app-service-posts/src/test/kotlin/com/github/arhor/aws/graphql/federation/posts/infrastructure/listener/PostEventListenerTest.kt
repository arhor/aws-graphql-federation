package com.github.arhor.aws.graphql.federation.posts.infrastructure.listener

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.service.OutboxMessageService
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
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

class PostEventListenerTest {

    private val outboxMessageService = mockk<OutboxMessageService>()
    private val postEventListener = PostEventListener(outboxMessageService)

    @MethodSource
    @ParameterizedTest
    fun `should call OutboxMessageService#storeAsOutboxMessage on any PostEvent`(
        // Given
        event: PostEvent,
    ) {
        every { outboxMessageService.storeAsOutboxMessage(any()) } just runs

        // When
        postEventListener.onPostEvent(event)

        // Then
        verify(exactly = 1) { outboxMessageService.storeAsOutboxMessage(event) }

        confirmVerified(outboxMessageService)
    }

    companion object {
        private val POST_ID = OMNI_UUID_VAL

        @JvmStatic
        fun `should call OutboxMessageService#storeAsOutboxMessage on any PostEvent`(): Stream<Arguments> = Stream.of(
            Arguments.of(PostEvent.Created(id = POST_ID)),
            Arguments.of(PostEvent.Deleted(id = POST_ID)),
        )
    }
}
