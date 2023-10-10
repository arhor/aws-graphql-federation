package com.github.arhor.aws.graphql.federation.tracing

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.CompletionStage
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Aspect
@Component
@ConditionalOnProperty(name = ["log-method-execution"], havingValue = "true")
class MethodExecutionLoggingAspect {

    @Around("@annotation(Trace) || @within(Trace)")
    fun logMethodExecution(joinPoint: ProceedingJoinPoint): Any? {
        val method = joinPoint.signature as MethodSignature
        val logger = LoggerFactory.getLogger(method.declaringTypeName)

        return if (logger.isDebugEnabled) {
            val methodName = method.name
            val methodArgs = joinPoint.args.contentToString()

            logger.debug(EXECUTION_START, methodName, methodArgs)

            with(Timer()) {
                when (val result = joinPoint.proceed()) {
                    is CompletionStage<*> -> result.whenComplete { success, failure ->
                        if (failure != null) {
                            logger.debug(EXECUTION_ERROR, methodName, failure.formattedWith(method), elapsedTime)
                        } else {
                            logger.debug(EXECUTION_CLOSE, methodName, success.formattedWith(method), elapsedTime)
                        }
                    }
                    else -> result.also {
                        logger.debug(EXECUTION_CLOSE, methodName, it.formattedWith(method), elapsedTime)
                    }
                }
            }
        } else {
            joinPoint.proceed()
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Any?.formattedWith(method: MethodSignature): String = when (method.returnType) {
        Void.TYPE -> VOID
        else -> toString()
    }

    @JvmInline
    private value class Timer(private val start: Long = System.currentTimeMillis()) {
        val elapsedTime get() = (System.currentTimeMillis() - start).toDuration(DurationUnit.MILLISECONDS)
    }

    companion object {
        private const val EXECUTION_START = "Method [START]: {}() >>> args: {}"
        private const val EXECUTION_CLOSE = "Method [CLOSE]: {}() <<< exit: {}, time: {}"
        private const val EXECUTION_ERROR = "Method [ERROR]: {}() <<< exit: {}, time: {}"
        private const val VOID = "void"
    }
}
