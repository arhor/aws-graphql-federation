package com.github.arhor.aws.graphql.federation.common

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

fun <T> ExecutorService.invokeAll(tasks: Collection<Callable<T>>, timeout: Duration): List<Future<T>> {
    return invokeAll(tasks, timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
}
