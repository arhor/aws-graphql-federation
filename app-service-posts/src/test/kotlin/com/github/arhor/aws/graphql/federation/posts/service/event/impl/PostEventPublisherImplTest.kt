package com.github.arhor.aws.graphql.federation.posts.service.event.impl

import com.github.arhor.aws.graphql.federation.common.event.PostEvent
import com.github.arhor.aws.graphql.federation.posts.config.props.AppProps
import com.github.arhor.aws.graphql.federation.posts.service.event.PostEventPublisher
import com.github.arhor.aws.graphql.federation.tracing.TRACING_ID_KEY
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
        PostEventPublisherImpl::class,
        PostEventPublisherImplTest.Config::class,
    ]
)
class PostEventPublisherImplTest {

    @EnableRetry
    @Configuration
    class Config

    @MockkBean
    private lateinit var appProps: AppProps

    @MockkBean
    private lateinit var sns: SnsOperations

    @Autowired
    private lateinit var postEventPublisher: PostEventPublisher

    @Test
    fun `should send outbox event as notifications to the SNS with correct payload and headers`() {
        // Given
        val traceId = UUID.randomUUID()
        val event = PostEvent.Deleted(id = UUID.randomUUID())

        val actualSnsTopicName = slot<String>()
        val actualNotification = slot<SnsNotification<*>>()

        every { appProps.aws.sns.postEvents } returns TEST_POST_EVENTS
        every { sns.sendNotification(capture(actualSnsTopicName), capture(actualNotification)) } just runs

        // When
        postEventPublisher.publish(event, traceId)

        // Then
        assertThat(actualSnsTopicName.captured)
            .isEqualTo(TEST_POST_EVENTS)

        assertThat(actualNotification.captured)
            .satisfies(
                { assertThat(it.payload).isEqualTo(event) },
                { assertThat(it.headers).isEqualTo(event.attributes(TRACING_ID_KEY to traceId)) },
            )
    }

    @Test
    fun `should retry on MessagingException sending notification to SNS`() {
        // Given
        val idempotencyKey = UUID.randomUUID()
        val event = PostEvent.Deleted(id = UUID.randomUUID())
        val error = MessagingException("Cannot deliver message during test!")
        val errors = listOf(error, error)

        every { appProps.aws.sns.postEvents } returns TEST_POST_EVENTS
        every { sns.sendNotification(any(), any()) } throwsMany errors andThenJust runs

        // When
        postEventPublisher.publish(event, idempotencyKey)

        // Then
        verify(exactly = 3) { appProps.aws.sns.postEvents }
        verify(exactly = 3) { sns.sendNotification(any(), any()) }

        confirmVerified(appProps, sns)
    }

    companion object {
        private const val TEST_POST_EVENTS = "test-post-events"

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
