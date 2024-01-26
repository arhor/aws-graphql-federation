package com.github.arhor.aws.graphql.federation.common

import java.math.BigDecimal
import java.math.BigInteger

fun Map<String, Any>.getLong(name: String): Long = when (val value = get(name)) {
    is BigInteger -> value.longValueExact()
    is BigDecimal -> value.longValueExact()
    is Number -> value.toLong()
    is String -> value.toLong()
    null -> throw IllegalArgumentException("Value for the key '$name' is missing!")
    else -> throw IllegalArgumentException("Value for the key '$name' cannot be converted to Long!")
}
