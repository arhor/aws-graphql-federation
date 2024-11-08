package com.github.arhor.aws.graphql.federation.users.service.tracing

import com.github.arhor.aws.graphql.federation.starter.tracing.StructuredArgumentFormatter
import com.github.arhor.aws.graphql.federation.users.data.model.UserEntity
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class UserEntityArgumentFormatter : StructuredArgumentFormatter<UserEntity>(
    UserEntity::username to { PROTECTED },
    UserEntity::password to { PROTECTED },
) {
    override val argumentType: KClass<UserEntity>
        get() = UserEntity::class
}
