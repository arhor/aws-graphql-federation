package com.github.arhor.aws.graphql.federation.users.test

import com.github.arhor.aws.graphql.federation.common.ZERO_UUID_STR
import org.springframework.core.annotation.AliasFor
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithSecurityContext
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@MustBeDocumented
@WithSecurityContext(factory = WithMockCurrentUserSecurityContextFactory::class)
annotation class WithMockCurrentUser(
    val value: String = ZERO_UUID_STR,
    val id: String = "",

    val roles: Array<out String> = ["USER"],
    val authorities: Array<out String> = [],

    @get:AliasFor(annotation = WithSecurityContext::class)
    val setupBefore: TestExecutionEvent = TestExecutionEvent.TEST_METHOD,
)
