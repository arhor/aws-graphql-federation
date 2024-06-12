package com.github.arhor.aws.graphql.federation.starter.core.data

import java.util.EnumSet

/**
 * Wrapper class over EnumSet is required to make it available for custom conversions.
 */
data class Features<F : Enum<F>>(
    val items: EnumSet<F>,
) {

    fun check(item: F): Boolean = item in items

    operator fun plus(item: F): Features<F> = Features(
        items = if (items.isEmpty()) {
            EnumSet.of(item)
        } else {
            EnumSet.copyOf(items).apply {
                add(item)
            }
        }
    )

    operator fun minus(item: F): Features<F> = Features(
        items = EnumSet.copyOf(items).apply {
            remove(item)
        }
    )

    fun toggle(item: F): Features<F> = when (check(item)) {
        true -> this - item
        else -> this + item
    }

    companion object {
        @JvmStatic
        fun <F : Enum<F>> of(item: F, vararg items: F): Features<F> =
            Features(items = EnumSet.of(item, *items))

        @JvmStatic
        fun <F : Enum<F>> emptyOf(type: Class<F>): Features<F> =
            Features(items = EnumSet.noneOf(type))
    }
}
