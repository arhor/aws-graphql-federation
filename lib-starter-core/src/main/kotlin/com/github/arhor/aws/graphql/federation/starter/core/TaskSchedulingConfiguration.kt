package com.github.arhor.aws.graphql.federation.starter.core

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties
import org.springframework.boot.task.SimpleAsyncTaskSchedulerBuilder
import org.springframework.boot.task.SimpleAsyncTaskSchedulerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler
import java.util.stream.Stream

@Configuration(proxyBeanMethods = false)
class TaskSchedulingConfiguration(
    private val properties: TaskSchedulingProperties,
    private val customizer: ObjectProvider<SimpleAsyncTaskSchedulerCustomizer>,
) {

    @Bean(name = ["simpleAsyncTaskSchedulerBuilder"])
    fun simpleAsyncTaskSchedulerBuilder(): SimpleAsyncTaskSchedulerBuilder =
        SimpleAsyncTaskSchedulerBuilder()
            .concurrencyLimit(properties.simple.concurrencyLimit)
            .threadNamePrefix(properties.threadNamePrefix)
            .virtualThreads(true)
            .customizers(customizer.orderedStream().asIterable())
            .apply {
                if (properties.shutdown.isAwaitTermination) {
                    taskTerminationTimeout(properties.shutdown.awaitTerminationPeriod)
                }
            }

    @Bean(name = ["taskScheduler"])
    fun taskScheduler(taskScheduler: SimpleAsyncTaskSchedulerBuilder): SimpleAsyncTaskScheduler =
        taskScheduler.build()

    private fun <T> Stream<T>.asIterable(): Iterable<T> = Iterable { iterator() }
}
