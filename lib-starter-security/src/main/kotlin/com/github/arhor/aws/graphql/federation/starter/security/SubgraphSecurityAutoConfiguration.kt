package com.github.arhor.aws.graphql.federation.starter.security

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor
import java.util.concurrent.Executor

@ComponentScan
@AutoConfiguration(before = [TaskExecutionAutoConfiguration::class])
class SubgraphSecurityAutoConfiguration {

    @Lazy
    @Bean(
        name = [
            TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME,
            AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME,
        ]
    )
    fun applicationTaskExecutor(builder: ThreadPoolTaskExecutorBuilder): Executor {
        return DelegatingSecurityContextExecutor(builder.build())
    }
}
