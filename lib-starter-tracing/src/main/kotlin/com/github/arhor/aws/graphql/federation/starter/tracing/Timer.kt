package com.github.arhor.aws.graphql.federation.starter.tracing

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@JvmInline
internal value class Timer(private val start: Long = System.currentTimeMillis()) {

    val elapsedTime: Duration
        get() = (System.currentTimeMillis() - start).toDuration(DurationUnit.MILLISECONDS)

    companion object {
        inline fun <R> start(block: Timer.() -> R): R = with(Timer(), block)
    }
}
