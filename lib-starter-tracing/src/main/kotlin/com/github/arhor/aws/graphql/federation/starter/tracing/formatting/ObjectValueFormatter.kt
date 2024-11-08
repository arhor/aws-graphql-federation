package com.github.arhor.aws.graphql.federation.starter.tracing.formatting

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

abstract class ObjectValueFormatter<T : Any>(
    private val overrides: Map<KProperty1<T, Any?>, (T) -> String>,
) : ValueFormatter<T> {

    @Lazy
    @Autowired
    private lateinit var rootFormatter: RootFormatter

    private val cachedMetadata by lazy {
        CachedMetadata(
            simpleName = valueType.simpleName ?: ANONYMOUS,
            properties = valueType.declaredMemberProperties,
        )
    }

    override fun format(value: T): String = buildString {
        val (
            simpleName: String,
            properties: Collection<KProperty1<T, *>>,
        ) = cachedMetadata

        append(simpleName)
        append("(")
        for ((index, property) in properties.withIndex()) {
            if (index > 0) {
                append(", ")
            }
            append(property.name)
            append("=")
            append(format(value, property))
        }
        append(")")
    }

    private fun format(value: T, property: KProperty1<T, Any?>): String? {
        return overrides[property]?.invoke(value)
            ?: property.get(value)?.let(rootFormatter::format)
    }

    private data class CachedMetadata<T>(
        val simpleName: String,
        val properties: Collection<KProperty1<T, Any?>>,
    )

    companion object {
        @JvmStatic
        protected val ANONYMOUS = "[ANONYMOUS]"

        @JvmStatic
        protected val PROTECTED = "[PROTECTED]"

        @JvmStatic
        protected fun <T : Any> protect(property: KProperty1<T, Any?>): Pair<KProperty1<T, Any?>, (T) -> String> {
            return property to { PROTECTED }
        }
    }
}
