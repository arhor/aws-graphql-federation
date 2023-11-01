package com.github.arhor.aws.graphql.federation.tracing

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.CompletionStage

@Aspect
@Component
@ConditionalOnProperty(name = ["log-method-execution"], havingValue = "true")
class MethodExecutionLoggingAspect {

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

        return if (logger.isDebugEnabled) {
            val methodName = method.name
            val methodArgs = jPoint.args.contentToString()

            logger.debug(EXECUTION_START, methodName, methodArgs)

            Timer.start {
                when (val result = jPoint.proceed()) {
                    is CompletionStage<*> -> result.whenComplete { success, failure ->
                        if (failure != null) {
                            logger.debug(EXECUTION_ERROR, methodName, failure, elapsedTime)
                        } else {
                            logger.debug(EXECUTION_CLOSE, methodName, success, elapsedTime)
                        }
                    }

                    else -> result.also {
                        logger.debug(EXECUTION_CLOSE, methodName, it, elapsedTime)
                    }
                }
            }
        } else {
            jPoint.proceed()
        }
    }

    companion object {
        private const val EXECUTION_START = "Method [START]: {}() >>> args: {}"
        private const val EXECUTION_CLOSE = "Method [CLOSE]: {}() <<< exit: {}, time: {}"
        private const val EXECUTION_ERROR = "Method [ERROR]: {}() <<< exit: {}, time: {}"
    }
}
