package com.github.arhor.aws.graphql.federation.starter.tracing.formatting

import kotlin.reflect.KClass

interface ValueFormatter<T : Any> {

    val valueType: KClass<T>

    fun format(value: T): String

    /**
     * Simple value formatter, using [Any.toString] method to format value.
     */
    companion object : ValueFormatter<Any> {
        /**
         * [Any] here means the formatter supports any type of values.
         */
        override val valueType: KClass<Any> = Any::class

        /**
         * Format returns default string representaton, defined by the value type.
         */
        override fun format(value: Any): String = value.toString()
    }
}

