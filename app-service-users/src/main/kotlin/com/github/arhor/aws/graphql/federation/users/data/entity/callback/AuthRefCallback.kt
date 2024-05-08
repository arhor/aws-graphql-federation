package com.github.arhor.aws.graphql.federation.users.data.entity.callback

import com.github.arhor.aws.graphql.federation.users.data.entity.AuthRef
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AuthRefCallback : BeforeConvertCallback<AuthRef> {

    override fun onBeforeConvert(aggregate: AuthRef): AuthRef {
        return if (aggregate.id == null) {
            aggregate.copy(id = UUID.randomUUID())
        } else {
            aggregate
        }
    }
}
