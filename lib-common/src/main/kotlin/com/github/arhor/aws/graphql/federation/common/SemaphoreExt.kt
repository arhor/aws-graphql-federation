package com.github.arhor.aws.graphql.federation.common

import java.util.concurrent.Semaphore

inline fun <T> Semaphore.use(action: () -> T): T {
    try {
        acquire()
        return action()
    } finally {
        release()
    }
}
