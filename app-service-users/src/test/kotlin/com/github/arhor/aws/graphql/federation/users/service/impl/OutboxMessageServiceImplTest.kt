package com.github.arhor.aws.graphql.federation.users.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.tracing.Attributes
import com.github.arhor.aws.graphql.federation.starter.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.TRACING_ID_KEY
import com.github.arhor.aws.graphql.federation.starter.tracing.useContextAttribute
import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.github.arhor.aws.graphql.federation.users.data.model.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxMessageRepository
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryOperations
import java.util.stream.Stream

private typealias OutboxEventData = TypeReference<Map<String, Any?>>

class OutboxMessageServiceImplTest {

    private val appProps = AppProps(
        aws = AppProps.Aws(
            sns = AppProps.Aws.Sns(
                appEvents = TEST_USER_EVENTS
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

    @AfterEach
    fun `confirm that all mocked dependencies interactions were verified`() {
        confirmVerified(
            objectMapper,
            outboxMessageRepository,
            snsRetryOperations,
            sns,
        )
    }

    @Nested
    @DisplayName("Method storeAsOutboxMessage")
    inner class StoreAsOutboxMessageTest {
        @MethodSource(USER_EVENTS_METHOD_SOURCE)
        @ParameterizedTest
        fun `should save UserEvent to the OutboxEventRepository`(
            // Given
            event: UserEvent,
        ) = mockkStatic(::useContextAttribute) {

            val expectedData = mockk<Map<String, Any?>>()
            val outboxMessageCapturingSlot = slot<OutboxMessageEntity>()

            every { objectMapper.convertValue(any(), any<OutboxEventData>()) } returns expectedData
            every { outboxMessageRepository.save(any()) } returns mockk()
            every { useContextAttribute(any()) } returns TRACE_ID

            // When
            outboxMessageService.storeAsOutboxMessage(event)

            // Then
            verify(exactly = 1) { objectMapper.convertValue(event, any<OutboxEventData>()) }
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
    @DisplayName("Method releaseOutboxMessagesOfType")
    inner class ReleaseOutboxMessagesOfTypeTest {
        @MethodSource(USER_EVENTS_METHOD_SOURCE)
        @ParameterizedTest
        fun `should publish UserEvent#Deleted using UserEventPublisher instance`(
            // Given
            event: UserEvent,
            eventType: UserEvent.Type,
        ) {
            // Given
            val eventData = mapOf("id" to USER_ID)
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

            every { outboxMessageRepository.findOldestMessagesWithLock(any(), any()) } returns messages
            every { objectMapper.convertValue(any(), any<Class<UserEvent>>()) } returns event
            every { snsRetryOperations.execute<Unit, Throwable>(any()) } answers {
                arg<RetryCallback<*, *>>(0).doWithRetry(null)
            }
            every { sns.sendNotification(any(), any()) } just runs
            every { outboxMessageRepository.deleteAll(any()) } just runs

            // When
            outboxMessageService.releaseOutboxMessagesOfType(eventType)

            // Then
            verify(exactly = 1) { outboxMessageRepository.findOldestMessagesWithLock(eventType.code, 50) }
            verify(exactly = 1) { objectMapper.convertValue(eventData, eventType.type.java) }
            verify(exactly = 1) { snsRetryOperations.execute<Unit, Throwable>(any()) }
            verify(exactly = 1) { sns.sendNotification(capture(actualSnsTopicName), capture(actualNotification)) }
            verify(exactly = 1) { outboxMessageRepository.deleteAll(messages) }

            assertThat(actualSnsTopicName.captured)
                .isEqualTo(TEST_USER_EVENTS)

            assertThat(actualNotification.captured)
                .satisfies(
                    { assertThat(it.payload).isEqualTo(event) },
                    { assertThat(it.headers).isEqualTo(expectedHeaders) },
                )
        }
    }

    companion object {
        private const val TEST_USER_EVENTS =
            "test-user-events"
        private const val USER_EVENTS_METHOD_SOURCE =
            "com.github.arhor.aws.graphql.federation.users.service.impl.OutboxMessageServiceImplTest#userEventsTestFactory"

        private val USER_ID = ZERO_UUID_VAL
        private val TRACE_ID = TEST_1_UUID_VAL
        private val MESSAGE_ID = TEST_2_UUID_VAL

        @JvmStatic
        fun userEventsTestFactory(): Stream<Arguments> = Stream.of(
            Arguments.of(UserEvent.Created(id = USER_ID), UserEvent.Type.USER_EVENT_CREATED),
            Arguments.of(UserEvent.Deleted(id = USER_ID), UserEvent.Type.USER_EVENT_DELETED),
        )
    }
}
