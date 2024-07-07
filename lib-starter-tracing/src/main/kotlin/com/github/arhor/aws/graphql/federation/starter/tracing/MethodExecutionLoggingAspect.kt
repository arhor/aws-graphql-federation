package com.github.arhor.aws.graphql.federation.starter.tracing

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.CompletionStage

@Aspect
@Component
@ConditionalOnProperty(prefix = "tracing.method-execution-logging", name = ["enabled"], havingValue = "true")
class MethodExecutionLoggingAspect(
    tracingProperties: TracingProperties,
) {
    private val logLevel: Level = tracingProperties.methodExecutionLogging.level

    // consider the following cases:
    //     1. return type is CompletionStage and joinPoint.proceed() was executed without exception and future contains success
    //     2. return type is CompletionStage and joinPoint.proceed() was executed without exception and future contains failure
    //     3. return type is CompletionStage and joinPoint.proceed() was executed with exception
    //     4. return type is something else and joinPoint.proceed() was executed without exception
    //     5. return type is something else and joinPoint.proceed() was executed with exception
    @Around("@annotation(Trace) || @within(Trace)")
    fun logMethodExecution(jPoint: ProceedingJoinPoint): Any? {
        val method = jPoint.signature as MethodSignature
        val logger = LoggerFactory.getLogger(method.declaringTypeName)

        val methodName = method.name
        val methodArgs = jPoint.args.contentToString()

        logger.atLevel(logLevel).log(EXECUTION_START, methodName, methodArgs)

        return if (logger.isEnabledForLevel(logLevel)) {
            Timer.start {
                try {
                    when (val result = jPoint.proceed()) {
                        is CompletionStage<*> -> {
                            result.whenComplete { success, failure ->
                                if (failure != null) {
                                    logger.failure(methodName, failure)
                                } else {
                                    logger.success(methodName, success)
                                }
                            }
                        }

                        else -> {
                            logger.success(methodName, result)
                            result
                        }
                    }
                } catch (error: Throwable) {
                    logger.failure(methodName, error)
                    throw error
                }
            }
        } else {
            jPoint.proceed()
        }
    }

    context(Timer)
    private fun Logger.success(methodName: String, value: Any?) {
        atLevel(logLevel).log(EXECUTION_CLOSE, methodName, value, elapsedTime)
    }

    context(Timer)
    private fun Logger.failure(methodName: String, error: Throwable) {
        atLevel(logLevel).log(EXECUTION_ERROR, methodName, error, elapsedTime)
    }

    companion object {
        private const val EXECUTION_START = "Method [START]: {}() >>> args: {}"
        private const val EXECUTION_CLOSE = "Method [CLOSE]: {}() <<< exit: {}, time: {}"
        private const val EXECUTION_ERROR = "Method [ERROR]: {}() <<< exit: {}, time: {}"
    }
}
