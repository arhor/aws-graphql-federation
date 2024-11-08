package com.github.arhor.aws.graphql.federation.starter.tracing.formatting

import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class ValueFormatterRegistry(valueFormatters: List<ValueFormatter<Any>>) {

    private val formattersByType: Map<KClass<Any>, ValueFormatter<Any>> =
        valueFormatters
            .associateBy { it.valueType }
            .withDefault { ValueFormatter }

    fun findFormatter(type: KClass<Any>): ValueFormatter<Any> =
        formattersByType.getValue(type)
}
