package com.github.arhor.aws.graphql.federation.posts.service.event.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.UUID
import java.util.stream.Stream

private typealias OutboxEventData = TypeReference<Map<String, Any?>>

class PostEventListenerImplTest {

    private val objectMapper = mockk<ObjectMapper>()
    private val outboxMessageRepository = mockk<OutboxMessageRepository>()

    private val userEventEmitter = PostEventListenerImpl(
        objectMapper,
        outboxMessageRepository,
    )

    @MethodSource
    @ParameterizedTest
    fun `should save PostEvent to the OutboxEventRepository`(
        // Given
        event: PostEvent,
    ) {
        every { objectMapper.convertValue(any(), any<OutboxEventData>()) } returns mockk()
        every { outboxMessageRepository.save(any()) } returns mockk()

        // When
        userEventEmitter.onPostEvent(event)

        // Then
        verify(exactly = 1) { objectMapper.convertValue(any(), any<OutboxEventData>()) }
        verify(exactly = 1) { outboxMessageRepository.save(any()) }
    }

    companion object {
        @JvmStatic
        fun `should save PostEvent to the OutboxEventRepository`(): Stream<Arguments> = Stream.of(
            arguments(PostEvent.Created(id = UUID.randomUUID())),
            arguments(PostEvent.Deleted(id = UUID.randomUUID())),
        )
    }
}
