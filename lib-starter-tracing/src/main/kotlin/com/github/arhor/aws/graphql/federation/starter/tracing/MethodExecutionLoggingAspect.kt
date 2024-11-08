package com.github.arhor.aws.graphql.federation.starter.tracing

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.CompletionStage
import kotlin.reflect.KClass

@Aspect
@Component
@ConditionalOnProperty(prefix = "tracing.method-execution-logging", name = ["enabled"], havingValue = "true")
class MethodExecutionLoggingAspect(
    tracingProperties: TracingProperties,
    argFormattersList: List<ArgumentFormatter<Any>>,
) {
    private val loggingLvl = tracingProperties.methodExecutionLogging.level
    private val formatters = argFormattersList.associateBy { it.argumentType }.withDefault { ArgumentFormatter }

    @Around("@annotation(Trace) || @within(Trace)")
    fun logMethodExecution(jPoint: ProceedingJoinPoint): Any? {
        val method = jPoint.signature as MethodSignature
        val logger = LoggerFactory.getLogger(method.declaringTypeName)

        if (logger.isEnabledForLevel(loggingLvl)) {
            val methodName = method.name
            val methodArgs = formatArgs(jPoint.args)

            logger.write(EXECUTION_START, methodName, methodArgs)

            Timer.start {
                val result = try {
                    jPoint.proceed()
                } catch (error: Throwable) {
                    logger.write(EXECUTION_ERROR, methodName, error, elapsedTime)
                    throw error
                }
                when (result) {
                    is CompletionStage<*> -> {
                        return result.whenComplete { success, failure ->
                            if (failure != null) {
                                logger.write(EXECUTION_ERROR, methodName, failure, elapsedTime)
                            } else {
                                logger.write(EXECUTION_CLOSE, methodName, success, elapsedTime)
                            }
                        }
                    }

                    else -> {
                        logger.write(EXECUTION_CLOSE, methodName, result, elapsedTime)
                        return result
                    }
                }
            }
        } else {
            aspectLogger.warn("Logging level '{}' configured for method execution logs, but is not enabled", loggingLvl)
            return jPoint.proceed()
        }
    }

    private fun formatArgs(args: Array<Any?>?): String =
        if (args.isNullOrEmpty()) {
            "[]"
        } else {
            args.joinToString(prefix = "[", postfix = "]", transform = ::formatArg)
        }

    @Suppress("UNCHECKED_CAST")
    private fun formatArg(arg: Any?): String =
        if (arg == null) {
            "null"
        } else {
            formatters.getValue(arg::class as KClass<Any>).format(arg)
        }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Logger.write(message: String, arg0: Any?, arg1: Any?): Unit =
        atLevel(loggingLvl).log(message, arg0, arg1)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Logger.write(message: String, vararg args: Any?): Unit =
        atLevel(loggingLvl).log(message, *args)

    companion object {
        private val aspectLogger = LoggerFactory.getLogger(this::class.java.enclosingClass)
        private const val EXECUTION_START = "Method [START]: {}() >>> args: {}"
        private const val EXECUTION_CLOSE = "Method [CLOSE]: {}() <<< exit: {}, time: {}"
        private const val EXECUTION_ERROR = "Method [ERROR]: {}() <<< exit: {}, time: {}"
    }
}
