package com.github.arhor.dgs.lib

import java.math.BigDecimal
import java.math.BigInteger

fun Map<String, Any>.getLong(name: String): Long {
    return when (val value = get(name)) {
        is BigInteger -> value.longValueExact()
        is BigDecimal -> value.longValueExact()
        is Number -> value.toLong()
        is String -> value.toLong()
        null -> throw IllegalArgumentException("Value for the key '$name' is missing!")
        else -> throw IllegalArgumentException("Value for the key '$name' cannot be converted to Long!")
    }
}