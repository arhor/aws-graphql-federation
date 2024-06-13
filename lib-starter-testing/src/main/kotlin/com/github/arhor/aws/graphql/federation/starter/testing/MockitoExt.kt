package com.github.arhor.aws.graphql.federation.starter.testing

import org.mockito.stubbing.Answer

fun <T> withFirstArg() = Answer<T> { it.getArgument(0) }
