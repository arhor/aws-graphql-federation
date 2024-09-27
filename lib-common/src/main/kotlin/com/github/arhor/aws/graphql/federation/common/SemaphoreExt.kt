package com.github.arhor.aws.graphql.federation.common

import com.github.arhor.aws.graphql.federation.common.sugar.was
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.time.Duration

inline fun <T> Semaphore.withPermit(timeout: Duration? = null, action: () -> T): T {
    if (timeout != null) {
        tryAcquire(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS).also { acquired ->
            if (was not acquired) {
                throw TimeoutException("Timeout of $timeout exceeded trying to acquire semaphore")
            }
        }
    } else {
        acquire()
    }
    try {
        return action()
    } finally {
        release()
    }
}
