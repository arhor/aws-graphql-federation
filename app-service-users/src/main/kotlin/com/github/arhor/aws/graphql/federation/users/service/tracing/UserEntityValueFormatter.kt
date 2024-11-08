package com.github.arhor.aws.graphql.federation.users.service.tracing

import com.github.arhor.aws.graphql.federation.starter.tracing.formatting.ObjectValueFormatter
import com.github.arhor.aws.graphql.federation.users.data.model.UserEntity
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class UserEntityValueFormatter : ObjectValueFormatter<UserEntity>(
    accessorOverrides = mapOf(
        UserEntity::username to { PROTECTED },
        UserEntity::password to { PROTECTED },
    ),
) {
    override val valueType: KClass<UserEntity>
        get() = UserEntity::class
}
