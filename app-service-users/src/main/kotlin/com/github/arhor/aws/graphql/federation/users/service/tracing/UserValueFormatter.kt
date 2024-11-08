package com.github.arhor.aws.graphql.federation.users.service.tracing

import com.github.arhor.aws.graphql.federation.starter.tracing.formatting.ObjectValueFormatter
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class UserValueFormatter : ObjectValueFormatter<User>(
    overrides = mapOf(
        User::username to { PROTECTED },
    ),
) {
    override val valueType: KClass<User>
        get() = User::class
}
