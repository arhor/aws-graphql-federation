package com.github.arhor.dgs.users.config

import org.slf4j.MDC
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME
import org.springframework.boot.task.TaskExecutorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor
import org.springframework.web.context.request.RequestContextHolder

@EnableAsync
@Configuration(proxyBeanMethods = false)
class ConfigureAsyncTasks : AsyncConfigurer {

    @Lazy
    @Autowired
    private lateinit var taskExecutorBuilder: TaskExecutorBuilder

    @Bean(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    override fun getAsyncExecutor() = DelegatingSecurityContextAsyncTaskExecutor(
        taskExecutorBuilder
            .build()
            .also { it.initialize() }
    )

    override fun getAsyncUncaughtExceptionHandler() = SimpleAsyncUncaughtExceptionHandler()

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
