package com.github.arhor.aws.graphql.federation.starter.core.time

import jakarta.validation.ClockProvider
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class TimeOperations(
    private val clockProvider: ClockProvider,
) {
    fun currentLocalDateTime(): LocalDateTime =
        LocalDateTime
            .now(clockProvider.clock)
            .truncated

    fun convertToLocalDateTime(instant: Instant): LocalDateTime =
        LocalDateTime
            .ofInstant(instant, clockProvider.clock.zone)
            .truncated

    private inline val LocalDateTime.truncated: LocalDateTime
        get() = truncatedTo(ChronoUnit.MILLIS)
}
