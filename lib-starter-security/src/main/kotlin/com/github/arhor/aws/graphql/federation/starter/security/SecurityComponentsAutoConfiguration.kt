package com.github.arhor.aws.graphql.federation.starter.security

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.task.TaskDecorator
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable

@ComponentScan
@AutoConfiguration(before = [TaskExecutionAutoConfiguration::class])
class SecurityComponentsAutoConfiguration {

    // What's happening here? Pretty easy to explain!
    //
    // I'm going to create my own bean representing Executor interface. Instead of using default
    // one, I've chosen to use default ThreadPoolTaskExecutorBuilder, but wrapping built executor
    // with DelegatingSecurityContextExecutor. Doing so I'm loosing all benefits of automatic bean
    // lifecycle management provided by ThreadPoolTaskExecutor.
    //
    // So, I decided to redeclare explicitly interfaces implemented by ThreadPoolTaskExecutor,
    // delegating their implementation to the wrapped instance, having at the same time all the
    // logic from DelegatingSecurityContextExecutor. Voilà!
//    @Lazy
//    @Bean(
//        name = [
//            APPLICATION_TASK_EXECUTOR_BEAN_NAME,
//            DEFAULT_TASK_EXECUTOR_BEAN_NAME,
//        ]
//    )
//    fun applicationTaskExecutor(executor: SimpleAsyncTaskExecutorBuilder): Executor =
//        executor.build().let {
//            object : DelegatingSecurityContextAsyncTaskExecutor(it),
//                BeanNameAware by it,
//                ApplicationContextAware by it,
//                InitializingBean by it,
//                DisposableBean by it,
//                SmartLifecycle by it,
//                ApplicationListener<ContextClosedEvent> by it {}
//        }

    // TODO: consider [org.springframework.core.task.support.CompositeTaskDecorator]
    @Bean
    fun delegatingSecurityContextRunnableTaskDecorator(): TaskDecorator = TaskDecorator {
        DelegatingSecurityContextRunnable(it)
    }
}
