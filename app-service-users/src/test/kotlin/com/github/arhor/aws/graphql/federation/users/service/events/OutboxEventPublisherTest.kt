package com.github.arhor.aws.graphql.federation.users.service.events

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
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
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.EnableRetry
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import kotlin.time.Duration.Companion.seconds

@SpringJUnitConfig
internal class OutboxEventPublisherTest {

    @EnableRetry
    @Configuration
    @ComponentScan(
        includeFilters = [Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [OutboxEventPublisher::class])],
        useDefaultFilters = false,
    )
    class Config

    @MockkBean
    private lateinit var appProps: AppProps

    @MockkBean
    private lateinit var sns: SnsOperations

    @Autowired
    private lateinit var outboxEventPublisher: OutboxEventPublisher


    @Test
    fun `should send outbox event as notifications to the SNS with correct payload and headers`() {
        // given
        val outboxEvent = OutboxEventEntity(
            type = UserEvent.USER_EVENT_DELETED,
            payload = emptyMap(),
            headers = emptyMap(),
        )

        val actualSnsTopicName = slot<String>()
        val actualNotification = slot<SnsNotification<*>>()

        // given
        every { appProps.aws.sns.userEvents } returns TEST_USER_EVENTS
        every { sns.sendNotification(capture(actualSnsTopicName), capture(actualNotification)) } just runs

        // when
        outboxEventPublisher.publish(outboxEvent)

        // then
        assertThat(actualSnsTopicName.captured)
            .isEqualTo(TEST_USER_EVENTS)

        assertThat(actualNotification.captured)
            .satisfies(
                { assertThat(it.payload).isEqualTo(outboxEvent.payload) },
                { assertThat(it.headers).isEqualTo(outboxEvent.headers) },
            )
    }

    @Test
    fun `should retry facing MessagingException sending notification to SNS`() {
        // given
        val outboxEvent = OutboxEventEntity(
            type = UserEvent.USER_EVENT_DELETED,
            payload = emptyMap(),
            headers = emptyMap(),
        )
        val error = MessagingException("Cannot deliver message during test!")
        val errors = listOf(error, error)

        every { appProps.aws.sns.userEvents } returns TEST_USER_EVENTS
        every { sns.sendNotification(any(), any()) } throwsMany errors andThenJust runs

        // when
        outboxEventPublisher.publish(outboxEvent)

        // then
        verify(exactly = 3) { appProps.aws.sns.userEvents }
        verify(exactly = 3) { sns.sendNotification(any(), any()) }

        confirmVerified(appProps, sns)
    }

    companion object {
        private const val TEST_USER_EVENTS = "test-user-events"

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
