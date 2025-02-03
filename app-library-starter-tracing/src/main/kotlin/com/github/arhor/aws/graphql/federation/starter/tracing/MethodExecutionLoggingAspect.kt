package com.github.arhor.aws.graphql.federation.starter.tracing

import com.github.arhor.aws.graphql.federation.starter.tracing.formatting.RootFormatter
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
    private val rootFormatter: RootFormatter,
    private val tracingProperties: TracingProperties,
) {

    @Around("@annotation(Trace) || @within(Trace)")
    fun logMethodExecution(jPoint: ProceedingJoinPoint): Any? {
        val logLvl = tracingProperties.methodExecutionLogging.level
        val method = jPoint.signature as MethodSignature
        val logger = LoggerFactory.getLogger(method.declaringTypeName)

        if (logger.isEnabledForLevel(logLvl)) {
            val methodName = method.name
            val methodArgs = jPoint.args

            logger.write(logLvl, EXECUTION_START, methodName, methodArgs)

            Timer.start {
                val result = try {
                    jPoint.proceed()
                } catch (error: Throwable) {
                    logger.write(logLvl, EXECUTION_ERROR, methodName, error, elapsedTime)
                    throw error
                }
                when (result) {
                    is CompletionStage<*> -> {
                        return result.whenComplete { success, failure ->
                            if (failure != null) {
                                logger.write(logLvl, EXECUTION_ERROR, methodName, failure, elapsedTime)
                            } else {
                                logger.write(logLvl, EXECUTION_CLOSE, methodName, success, elapsedTime)
                            }
                        }
                    }

                    else -> {
                        logger.write(logLvl, EXECUTION_CLOSE, methodName, result, elapsedTime)
                        return result
                    }
                }
            }
        } else {
            aspectLogger.warn("Logging level '{}' configured for method execution logs, but is not enabled", logLvl)
            return jPoint.proceed()
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Logger.write(level: Level, message: String, arg0: Any?, arg1: Any?): Unit =
        atLevel(level).log(message, format(arg0), format(arg1))

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Logger.write(level: Level, message: String, vararg args: Any?): Unit =
        atLevel(level).log(message, *Array(args.size) { format(args[it]) })

    @Suppress("NOTHING_TO_INLINE")
    private inline fun format(arg: Any?): String =
        if (arg != null) rootFormatter.format(arg) else "null"

    companion object {
        private val aspectLogger = LoggerFactory.getLogger(this::class.java.enclosingClass)
        private const val EXECUTION_START = "Method [START]: {}() >>> args: {}"
        private const val EXECUTION_CLOSE = "Method [CLOSE]: {}() <<< exit: {}, time: {}"
        private const val EXECUTION_ERROR = "Method [ERROR]: {}() <<< exit: {}, time: {}"
    }
}
