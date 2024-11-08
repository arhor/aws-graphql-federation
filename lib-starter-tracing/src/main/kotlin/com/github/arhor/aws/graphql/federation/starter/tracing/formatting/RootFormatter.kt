package com.github.arhor.aws.graphql.federation.starter.tracing.formatting

import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class RootFormatter(valueFormatters: List<ValueFormatter<Any>>) {

    private val formattersByType: Map<KClass<Any>, ValueFormatter<Any>> =
        valueFormatters
            .associateBy { it.valueType }
            .withDefault { ValueFormatter }

    @Suppress("UNCHECKED_CAST")
    fun format(value: Any): String =
        formattersByType
            .getValue(value::class as KClass<Any>)
            .format(value)
}
