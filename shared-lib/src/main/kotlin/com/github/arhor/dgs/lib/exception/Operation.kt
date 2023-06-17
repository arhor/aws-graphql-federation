package com.github.arhor.dgs.lib.exception

enum class Operation {
    CREATE,
    READ,
    UPDATE,
    DELETE,
}

fun Operation?.toString() = this?.toString() ?: "UNKNOWN"
