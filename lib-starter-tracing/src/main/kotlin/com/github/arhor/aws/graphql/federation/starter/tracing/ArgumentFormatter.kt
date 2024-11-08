package com.github.arhor.aws.graphql.federation.starter.tracing

import kotlin.reflect.KClass

interface ArgumentFormatter<T : Any> {

    val argumentType: KClass<T>

    fun format(arg: T): String

    /**
     * Simple argument formatter, using [Any.toString] method to format argument value.
     */
    companion object : ArgumentFormatter<Any> {
        /**
         * [Any] here means the formatter supports any type of argument.
         */
        override val argumentType: KClass<Any> = Any::class

        /**
         * Format returns default string representaton, defined by the argument type.
         */
        override fun format(arg: Any): String = arg.toString()
    }
}

