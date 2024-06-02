package com.github.arhor.aws.graphql.federation.spring.core.data

import java.util.EnumSet

/**
 * Wrapper class over EnumSet is required to make it available for custom conversions.
 */
data class Features<E : Enum<E>>(
    val items: EnumSet<E>,
) {
    constructor(item: E, vararg items: E) : this(items = EnumSet.of(item, *items))

    fun check(feature: E): Boolean = feature in items

    operator fun plus(item: E): Features<E> = Features(
        items = if (items.isEmpty()) {
            EnumSet.of(item)
        } else {
            EnumSet.copyOf(items).apply {
                add(item)
            }
        }
    )
}
