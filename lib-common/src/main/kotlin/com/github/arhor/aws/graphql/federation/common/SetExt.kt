package com.github.arhor.aws.graphql.federation.common

fun <T, R> Iterable<T>.toSet(transform: (T) -> R): Set<R> = mapTo(LinkedHashSet(), transform)