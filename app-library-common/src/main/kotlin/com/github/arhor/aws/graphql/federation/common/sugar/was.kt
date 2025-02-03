@file:Suppress("ClassName", "NOTHING_TO_INLINE")

package com.github.arhor.aws.graphql.federation.common.sugar

object was {
    inline infix fun not(value: Boolean): Boolean = !value
}
