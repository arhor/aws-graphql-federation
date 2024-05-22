package com.github.arhor.aws.graphql.federation.users.service.event.impl

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.tracing.IDEMPOTENT_KEY
import com.github.arhor.aws.graphql.federation.tracing.TRACING_ID_KEY
import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.github.arhor.aws.graphql.federation.users.service.event.UserEventPublisher
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.andThenJust
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.EnableRetry
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@SpringJUnitConfig(
    classes = [
        UserEventPublisherImpl::class,
        UserEventPublisherImplTest.Config::class,
    ]
)
class UserEventPublisherImplTest {

    @EnableRetry
    @Configuration
    class Config

    @MockkBean
    private lateinit var appProps: AppProps

    @MockkBean
    private lateinit var sns: SnsOperations

    @Autowired
    private lateinit var userEventPublisher: UserEventPublisher

    @Test
    fun `should send outbox event as notifications to the SNS with correct payload and headers`() {
        // Given
        val event = UserEvent.Deleted(id = userId)
        val expectedHeaders = event.attributes(
            TRACING_ID_KEY to traceId.toString(),
            IDEMPOTENT_KEY to idempotentKey.toString(),
        )

        val actualSnsTopicName = slot<String>()
        val actualNotification = slot<SnsNotification<*>>()

        every { appProps.aws!!.sns!!.userEvents } returns TEST_USER_EVENTS
        every { sns.sendNotification(capture(actualSnsTopicName), capture(actualNotification)) } just runs

        // When
        userEventPublisher.publish(event, traceId, idempotentKey)

        // Then
        assertThat(actualSnsTopicName.captured)
            .isEqualTo(TEST_USER_EVENTS)

        assertThat(actualNotification.captured)
            .satisfies(
                { assertThat(it.payload).isEqualTo(event) },
                { assertThat(it.headers).isEqualTo(expectedHeaders) },
            )
    }

    @Test
    fun `should retry on MessagingException sending notification to SNS`() {
        // Given
        val event = UserEvent.Deleted(id = userId)
        val error = MessagingException("Cannot deliver message during test!")
        val errors = listOf(error, error)

        every { appProps.aws!!.sns!!.userEvents } returns TEST_USER_EVENTS
        every { sns.sendNotification(any(), any()) } throwsMany errors andThenJust runs

        // When
        userEventPublisher.publish(event, traceId, idempotentKey)

        // Then
        verify(exactly = 3) { appProps.aws!!.sns!!.userEvents }
        verify(exactly = 3) { sns.sendNotification(any(), any()) }

        confirmVerified(appProps, sns)
    }

    companion object {
        private const val TEST_USER_EVENTS = "test-user-events"

        private val userId = UUID.randomUUID()
        private val traceId = UUID.randomUUID()
        private val idempotentKey = UUID.randomUUID()

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("app-props.retry.delay") { 1.seconds.inWholeMilliseconds }
                add("app-props.retry.multiplier") { 1 }
                add("app-props.retry.max-attempts") { 3 }
            }
        }
    }
}
