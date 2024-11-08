package com.github.arhor.aws.graphql.federation.starter.tracing

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

abstract class StructuredArgumentFormatter<T : Any>(
    vararg customAccessors: Pair<KProperty1<T, Any?>, (T) -> Any?>,
) : ArgumentFormatter<T> {

    private val accessorOverrides = customAccessors.toMap()

    override fun format(arg: T): String = buildString {
        append(argumentType.simpleName ?: ANONYMOUS)
        append("(")

        val properties = argumentType.declaredMemberProperties
        val finalIndex = properties.size - 1

        for ((index, property) in properties.withIndex()) {
            append(property.name)
            append("=")
            append(
                when (val acessor = accessorOverrides[property]) {
                    null -> property.get(arg)
                    else -> acessor.invoke(arg)
                }
            )
            if (index < finalIndex) {
                append(", ")
            }
        }
        append(")")
    }

    companion object {
        @JvmStatic
        protected val ANONYMOUS = "[ANONYMOUS]"

        @JvmStatic
        protected val PROTECTED = "[PROTECTED]"
    }
}
