package com.github.arhor.dgs.comments.api.listener

import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean

internal class BaseSqsListenerTest {

    class Config {
        @Bean
        fun kotlinModuleBean() = kotlinModule()
    }

    companion object {
        const val USER_UPDATED_TEST_EVENTS_QUEUE = "user-updated-test-events"
        const val USER_DELETED_TEST_EVENTS_QUEUE = "user-deleted-test-events"
        const val POST_UPDATED_TEST_EVENTS_QUEUE = "post-updated-test-events"
        const val POST_DELETED_TEST_EVENTS_QUEUE = "post-deleted-test-events"
    }
}
