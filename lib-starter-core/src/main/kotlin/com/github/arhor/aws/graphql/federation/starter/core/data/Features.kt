package com.github.arhor.aws.graphql.federation.starter.core.data

import java.util.EnumSet

/**
 * Wrapper class over EnumSet is required to make it available for custom conversions.
 */
abstract class Features<F : Features<F, E>, E : Enum<E>>(
    val items: EnumSet<E>,
) {

    protected abstract fun create(items: EnumSet<E>): F

    fun check(item: E): Boolean = item in items

    operator fun plus(item: E): F = create(
        items = if (items.isEmpty()) {
            EnumSet.of(item)
        } else {
            EnumSet.copyOf(items).apply {
                add(item)
            }
        }
    )

    operator fun minus(item: E): F = create(
        items = EnumSet.copyOf(items).apply {
            remove(item)
        }
    )

    fun toggle(item: E): F = when (check(item)) {
        true -> this - item
        else -> this + item
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Features<*, *>

        return items == other.items
    }

    override fun hashCode(): Int {
        return items.hashCode()
    }

    override fun toString(): String {
        return items.toString()
    }
}
