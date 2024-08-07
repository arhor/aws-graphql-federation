package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import com.github.arhor.aws.graphql.federation.posts.data.entity.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.posts.service.impl.OutboxMessageServiceImpl.Companion.OutboxMessageDataTypeRef
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.tracing.Attributes
import com.github.arhor.aws.graphql.federation.starter.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.TRACING_ID_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.useContextAttribute
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryOperations
import java.util.stream.Stream

class OutboxMessageServiceImplTest {

    private val appProps = AppProps(
        aws = AppProps.Aws(
            sns = AppProps.Aws.Sns(
                appEvents = TEST_APPLICATION_EVENT_BUS
            ),
            sqs = AppProps.Aws.Sqs(
                syncPostsOnUserCreatedEvent = "test-user-created-events",
                syncPostsOnUserDeletedEvent = "test-user-deleted-events",
            )
        ),
        retry = AppProps.Retry()
    )

    private val objectMapper = mockk<ObjectMapper>()
    private val outboxMessageRepository = mockk<OutboxMessageRepository>()
    private val snsRetryOperations = mockk<RetryOperations>()
    private val sns = mockk<SnsOperations>()

    private val outboxMessageService = OutboxMessageServiceImpl(
        appProps,
        objectMapper,
        outboxMessageRepository,
        snsRetryOperations,
        sns,
    )

    @Nested
    @DisplayName("OutboxMessageService :: storeAsOutboxMessage")
    inner class StoreAsOutboxMessageTest {
        @MethodSource(POST_EVENTS_METHOD_SOURCE)
        @ParameterizedTest
        fun `should save PostEvent to the OutboxEventRepository`(
            // Given
            event: PostEvent,
        ) = mockkStatic(::useContextAttribute) {

            val expectedData = mockk<Map<String, Any?>>()
            val outboxMessageCapturingSlot = slot<OutboxMessageEntity>()

            every { objectMapper.convertValue(any(), any<TypeReference<*>>()) } returns expectedData
            every { outboxMessageRepository.save(any()) } returns mockk()
            every { useContextAttribute(any()) } returns TRACE_ID

            // When
            outboxMessageService.storeAsOutboxMessage(event)

            // Then
            verify(exactly = 1) { objectMapper.convertValue(event, OutboxMessageDataTypeRef) }
            verify(exactly = 1) { outboxMessageRepository.save(capture(outboxMessageCapturingSlot)) }
            verify(exactly = 1) { useContextAttribute(Attributes.TRACING_ID) }

            assertThat(outboxMessageCapturingSlot.captured)
                .isNotNull()
                .returns(event.type(), from { it.type })
                .returns(expectedData, from { it.data })
                .returns(TRACE_ID, from { it.traceId })
        }
    }

    @Nested
    @DisplayName("OutboxMessageService :: releaseOutboxMessagesOfType")
    inner class ReleaseOutboxMessagesOfTypeTest {
        @MethodSource(POST_EVENTS_METHOD_SOURCE)
        @ParameterizedTest
        fun `should publish PostEvent to SNS using RetryOperations`(
            // Given
            event: PostEvent,
            eventType: PostEvent.Type,
        ) {
            // Given
            val eventData = mapOf("id" to POST_ID)
            val message = OutboxMessageEntity(
                id = MESSAGE_ID,
                type = event.type(),
                data = eventData,
                traceId = TRACE_ID,
            )
            val messages = listOf(message)

            val expectedHeaders = event.attributes(
                TRACING_ID_KEY to TRACE_ID.toString(),
                IDEMPOTENT_KEY to MESSAGE_ID.toString(),
            )

            val actualSnsTopicName = slot<String>()
            val actualNotification = slot<SnsNotification<*>>()

            every { outboxMessageRepository.dequeueOldest(any(), any()) } returns messages
            every { objectMapper.convertValue(any(), any<Class<PostEvent>>()) } returns event
            every { snsRetryOperations.execute<Unit, Throwable>(any()) } answers {
                arg<RetryCallback<*, *>>(0).doWithRetry(
                    null
                )
            }
            every { sns.sendNotification(any(), any()) } just runs

            // When
            outboxMessageService.releaseOutboxMessagesOfType(eventType)

            // Then
            verify(exactly = 1) { outboxMessageRepository.dequeueOldest(eventType.code, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, eventType.type.java) }
            verify(exactly = 1) { snsRetryOperations.execute<Unit, Throwable>(any()) }
            verify(exactly = 1) { sns.sendNotification(capture(actualSnsTopicName), capture(actualNotification)) }


            assertThat(actualSnsTopicName.captured)
                .isEqualTo(TEST_APPLICATION_EVENT_BUS)

            assertThat(actualNotification.captured)
                .satisfies(
                    { assertThat(it.payload).isEqualTo(event) },
                    { assertThat(it.headers).isEqualTo(expectedHeaders) },
                )
        }
    }

    companion object {
        private const val TEST_APPLICATION_EVENT_BUS =
            "test-app-events"
        private const val POST_EVENTS_METHOD_SOURCE =
            "com.github.arhor.aws.graphql.federation.posts.service.impl.OutboxMessageServiceImplTest#postEventsTestFactory"

        private val USER_ID = ZERO_UUID_VAL
        private val POST_ID = OMNI_UUID_VAL
        private val TRACE_ID = TEST_1_UUID_VAL
        private val MESSAGE_ID = TEST_2_UUID_VAL

        @JvmStatic
        @Suppress("unused")
        fun postEventsTestFactory(): Stream<Arguments> = Stream.of(
            Arguments.of(PostEvent.Created(id = POST_ID, userId = USER_ID), PostEvent.Type.POST_EVENT_CREATED),
            Arguments.of(PostEvent.Deleted(id = POST_ID), PostEvent.Type.POST_EVENT_DELETED),
        )
    }
}
