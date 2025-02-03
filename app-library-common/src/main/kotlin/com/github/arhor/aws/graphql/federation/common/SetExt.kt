package com.github.arhor.aws.graphql.federation.common

fun <T, R> Iterable<T>.toSet(transform: (T) -> R): Set<R> = mapTo(LinkedHashSet(), transform)
fun <T, R> Iterable<T>.flattenToSet(transform: (T) -> Iterable<R>): Set<R> = flatMapTo(LinkedHashSet(), transform)
