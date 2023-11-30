package com.github.arhor.aws.graphql.federation.async

import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.web.context.request.RequestContextHolder

@Configuration(proxyBeanMethods = false)
class ConfigureTasksExecution {

    @Bean
    fun parentContextTaskDecorator() = TaskDecorator {
        val attributes = RequestContextHolder.getRequestAttributes()
        val contextMap = MDC.getCopyOfContextMap()

        if (attributes == null && contextMap == null) it else Runnable {
            try {
                RequestContextHolder.setRequestAttributes(attributes)
                MDC.setContextMap(contextMap ?: emptyMap())
                it.run()
            } finally {
                MDC.clear()
                RequestContextHolder.resetRequestAttributes()
            }
        }
    }
}
