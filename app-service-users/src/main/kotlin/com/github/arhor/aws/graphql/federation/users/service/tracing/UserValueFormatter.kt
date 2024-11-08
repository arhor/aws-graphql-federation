package com.github.arhor.aws.graphql.federation.users.service.tracing

import com.github.arhor.aws.graphql.federation.starter.tracing.formatting.ObjectValueFormatter
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class UserValueFormatter : ObjectValueFormatter<User>(
    overrides = mapOf(
        protect(User::username),
    ),
) {
    override val valueType: KClass<User>
        get() = User::class
}
