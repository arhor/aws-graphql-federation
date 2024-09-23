package com.github.arhor.aws.graphql.federation.common

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

const val UNBOUNDED = 0

fun <T, R> List<T>.async(parallelism: Int = UNBOUNDED, timeout: Duration? = null, action: (T) -> R): List<Future<R>> {
    require(parallelism >= UNBOUNDED)
    require(timeout?.isNegative() != true)

    val vExecutor = Executors.newVirtualThreadPerTaskExecutor()

    val tasks = if (parallelism > UNBOUNDED) {
        with(receiver = Semaphore(parallelism)) {
            map {
                Callable {
                    acquire()
                    try {
                        action.invoke(it)
                    } finally {
                        release()
                    }
                }
            }
        }
    } else {
        map { Callable { action.invoke(it) } }
    }
    return if (timeout != null) {
        vExecutor.invokeAll(tasks, timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
    } else {
        vExecutor.invokeAll(tasks)
    }
}

fun <T> List<Future<T>>.await(ignoreExceptions: Boolean = false): List<T> {
    val futures = if (ignoreExceptions) {
        this.filter { it.state() == Future.State.SUCCESS }
    } else {
        this
    }
    return futures.map(Future<T>::get)
}
