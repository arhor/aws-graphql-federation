@file:Suppress("ClassName")

package com.github.arhor.aws.graphql.federation.users.service.events

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.users.data.entity.OutboxEventEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.OutboxEventRepository
import com.github.arhor.aws.graphql.federation.users.test.ConfigureTestObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
internal class UserEventEmitterTest {

    @Configuration
    @ComponentScan(
        includeFilters = [Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [UserEventEmitter::class])],
        useDefaultFilters = false,
    )
    @Import(ConfigureTestObjectMapper::class)
    class Config

    @MockkBean
    private lateinit var outboxEventRepository: OutboxEventRepository

    @Autowired
    private lateinit var userEventEmitter: UserEventEmitter

    @Test
    fun `should save UserEvent to the OutboxEventRepository`() {
        // given
        val event = UserEvent.Deleted(id = 1L)

        val expectedType = event.type()
        val expectedPayload = mapOf(UserEvent.Deleted::id.name to event.id)
        val expectedHeaders = event.attributes()

        val actualOutEvent = slot<OutboxEventEntity>()

        every { outboxEventRepository.save(capture(actualOutEvent)) } returns mockk()

        // when
        userEventEmitter.emit(event)

        // then
        assertThat(actualOutEvent.captured)
            .returns(expectedType, from { it.type })
            .returns(expectedPayload, from { it.payload })
            .returns(expectedHeaders, from { it.headers })
    }
}
