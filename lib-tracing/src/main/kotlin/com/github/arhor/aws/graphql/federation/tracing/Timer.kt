package com.github.arhor.aws.graphql.federation.tracing

import kotlin.time.DurationUnit
import kotlin.time.toDuration

@JvmInline
internal value class Timer(private val start: Long = System.currentTimeMillis()) {
    val elapsedTime get() = (System.currentTimeMillis() - start).toDuration(DurationUnit.MILLISECONDS)
}
