package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.arhor.aws.graphql.federation.common.constants.Attributes
import com.github.arhor.aws.graphql.federation.common.event.AppEvent
import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import com.github.arhor.aws.graphql.federation.posts.data.model.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.OutboxMessageRepository
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.tracing.useContextAttribute
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryOperations
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class OutboxMessageServiceImplTest {

    private val appProps = AppProps(
        events = AppProps.Events(
            target = AppProps.Events.Target(
                appEvents = TEST_APPLICATION_EVENT_BUS
            ),
            source = AppProps.Events.Source(
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

    @AfterEach
    fun `confirm that all mocked dependencies interactions were verified`() {
        confirmVerified(
            objectMapper,
            outboxMessageRepository,
            snsRetryOperations,
            sns,
        )
    }

    @MethodSource
    @ParameterizedTest
    fun `storeToOutboxMessages should save PostEvent to the OutboxEventRepository`(
        // Given
        event: PostEvent,
    ) = mockkStatic(::useContextAttribute) {

        val expectedData = mockk<Map<String, Any?>>()
        val outboxMessageCapturingSlot = slot<OutboxMessageEntity>()

        every { objectMapper.convertValue(any(), any<TypeReference<Map<String, Any?>>>()) } returns expectedData
        every { outboxMessageRepository.save(any()) } returns mockk()
        every { useContextAttribute(any()) } returns TRACE_ID

        // When
        outboxMessageService.storeToOutboxMessages(event)

        // Then
        verify(exactly = 1) { objectMapper.convertValue(event, any<TypeReference<Map<String, Any?>>>()) }
        verify(exactly = 1) { outboxMessageRepository.save(capture(outboxMessageCapturingSlot)) }
        verify(exactly = 1) { useContextAttribute(Attributes.TRACE_ID) }

        assertThat(outboxMessageCapturingSlot.captured)
            .isNotNull()
            .returns(event.type(), from { it.type })
            .returns(expectedData, from { it.data })
            .returns(TRACE_ID, from { it.traceId })
    }

    @Test
    fun `releaseOutboxMessages should publish PostEvent to SNS using RetryOperations`() {
        // Given
        val eventData = mapOf("id" to POST_ID)
        val message = OutboxMessageEntity(
            id = MESSAGE_ID,
            type = "x-test-event",
            data = eventData,
            traceId = TRACE_ID,
        )
        val messages = listOf(message)
        val messageIds = messages.mapNotNull { it.id }

        val expectedMessagesNum = 50
        val expectedHeaders = AppEvent.attributes("x-test-event", TRACE_ID.toString())

        val actualSnsTopicName = slot<String>()
        val actualNotification = slot<SnsNotification<*>>()

        every { outboxMessageRepository.findOldestMessagesWithLock(any()) } returns messages
        every { snsRetryOperations.execute<Unit, Throwable>(any()) } answers {
            arg<RetryCallback<*, *>>(0).doWithRetry(null)
        }
        every { sns.sendNotification(any(), any()) } just runs
        every { outboxMessageRepository.deleteAllById(any()) } just runs

        // When
        outboxMessageService.releaseOutboxMessages(expectedMessagesNum)

        // Then
        verify(exactly = 1) { outboxMessageRepository.findOldestMessagesWithLock(expectedMessagesNum) }
        verify(exactly = 1) { snsRetryOperations.execute<Unit, Throwable>(any()) }
        verify(exactly = 1) { sns.sendNotification(capture(actualSnsTopicName), capture(actualNotification)) }
        verify(exactly = 1) { outboxMessageRepository.deleteAllById(messageIds) }

        assertThat(actualSnsTopicName.captured)
            .isEqualTo(TEST_APPLICATION_EVENT_BUS)

        assertThat(actualNotification.captured)
            .satisfies(
                { assertThat(it.payload).isEqualTo(eventData) },
                { assertThat(it.headers).isEqualTo(expectedHeaders) },
            )
    }

    companion object {
        private const val TEST_APPLICATION_EVENT_BUS = "test-app-events"

        private val USER_ID = ZERO_UUID_VAL
        private val POST_ID = OMNI_UUID_VAL
        private val TRACE_ID = TEST_1_UUID_VAL
        private val MESSAGE_ID = TEST_2_UUID_VAL

        @JvmStatic
        fun `storeToOutboxMessages should save PostEvent to the OutboxEventRepository`(): Stream<Arguments> = Stream.of(
            Arguments.of(PostEvent.Created(id = POST_ID, userId = USER_ID), PostEvent.Type.POST_EVENT_CREATED),
            Arguments.of(PostEvent.Deleted(id = POST_ID), PostEvent.Type.POST_EVENT_DELETED),
        )
    }
}
