package com.github.arhor.aws.graphql.federation.common

import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

fun Map<String, Any>.getLong(name: String): Long = when (val value = get(name)) {
    is BigInteger -> value.longValueExact()
    is BigDecimal -> value.longValueExact()
    is Number -> value.toLong()
    is String -> value.toLong()
    null -> throw IllegalArgumentException("Value for the key '$name' is missing!")
    else -> throw IllegalArgumentException("Value for the key '$name' cannot be converted to Long!")
}

fun Map<String, Any>.getUuid(name: String): UUID = when (val value = get(name)) {
    is UUID -> value
    is String -> UUID.fromString(value)
    null -> throw IllegalArgumentException("Value for the key '$name' is missing!")
    else -> throw IllegalArgumentException("Value for the key '$name' cannot be converted to UUID!")
}
