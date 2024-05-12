package com.github.arhor.aws.graphql.federation.posts.service.event.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.tracing.Attributes
import com.github.arhor.aws.graphql.federation.tracing.useContextAttribute
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
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
        traceId: UUID,
    ) = mockkStatic(::useContextAttribute) {

        val expectedData = mockk<Map<String, Any?>>()
        val outboxMessageCapturingSlot = slot<OutboxMessageEntity>()

        every { objectMapper.convertValue(any(), any<OutboxEventData>()) } returns expectedData
        every { outboxMessageRepository.save(any()) } returns mockk()
        every { useContextAttribute(any()) } returns traceId

        // When
        userEventEmitter.onPostEvent(event)

        // Then
        verify(exactly = 1) { objectMapper.convertValue(event, any<OutboxEventData>()) }
        verify(exactly = 1) { outboxMessageRepository.save(capture(outboxMessageCapturingSlot)) }
        verify(exactly = 1) { useContextAttribute(Attributes.TRACING_ID) }

        assertThat(outboxMessageCapturingSlot.captured)
            .isNotNull()
            .returns(event.type(), from { it.type })
            .returns(expectedData, from { it.data })
            .returns(traceId, from { it.traceId })
    }

    companion object {
        @JvmStatic
        fun `should save PostEvent to the OutboxEventRepository`(): Stream<Arguments> = Stream.of(
            arguments(PostEvent.Created(id = UUID.randomUUID()), UUID.randomUUID()),
            arguments(PostEvent.Deleted(id = UUID.randomUUID()), UUID.randomUUID()),
        )
    }
}
