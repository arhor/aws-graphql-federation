@file:Suppress("ClassName")

package com.github.arhor.aws.graphql.federation.users.service.events

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventEmitter
import com.github.arhor.aws.graphql.federation.users.config.props.AppProps
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.andThenJust
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
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
internal class UserEventEmitterTest {

    @EnableRetry
    @Configuration
    @ComponentScan(
        useDefaultFilters = false, includeFilters = [
            Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [UserEventEmitter::class])
        ]
    )
    class Config

    @MockkBean
    private lateinit var appProps: AppProps

    @MockkBean
    private lateinit var sns: SnsOperations

    @Autowired
    private lateinit var userEventEmitter: UserEventEmitter

    @Test
    fun `should send notification even after several sequential failures with messaging`() {
        // Given
        val event = mockk<UserEvent>()
        val error = MessagingException("Cannot deliver message during test!")
        val errors = listOf(error, error)

        val snsTopicName = slot<String>()
        val notification = slot<SnsNotification<UserEvent>>()

        every { event.attributes() } returns mapOf("type" to "test-event")
        every { appProps.aws.sns.userEvents } returns TEST_USER_EVENTS
        every { sns.sendNotification(capture(snsTopicName), capture(notification)) } throwsMany errors andThenJust runs

        // When
        userEventEmitter.emit(event)

        // Then
        verify(exactly = 3) { appProps.aws.sns.userEvents }
        verify(exactly = 3) { sns.sendNotification(any(), any()) }

        assertThat(snsTopicName.captured)
            .isEqualTo(TEST_USER_EVENTS)

        assertThat(notification.captured)
            .satisfies(
                { assertThat(it.payload).isEqualTo(event) },
                { assertThat(it.headers).containsAllEntriesOf(event.attributes()) },
            )

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
