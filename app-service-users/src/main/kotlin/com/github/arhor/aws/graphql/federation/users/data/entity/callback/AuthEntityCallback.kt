package com.github.arhor.aws.graphql.federation.users.data.entity.callback

import com.github.arhor.aws.graphql.federation.users.data.entity.AuthEntity
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AuthEntityCallback : BeforeConvertCallback<AuthEntity> {

    override fun onBeforeConvert(aggregate: AuthEntity): AuthEntity {
        return if (aggregate.id == null) {
            aggregate.copy(id = UUID.randomUUID())
        } else {
            aggregate
        }
    }
}
