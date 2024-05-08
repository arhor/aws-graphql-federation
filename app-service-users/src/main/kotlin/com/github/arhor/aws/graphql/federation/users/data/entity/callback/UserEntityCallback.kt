package com.github.arhor.aws.graphql.federation.users.data.entity.callback

import com.github.arhor.aws.graphql.federation.users.data.entity.UserEntity
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserEntityCallback : BeforeConvertCallback<UserEntity> {

    override fun onBeforeConvert(aggregate: UserEntity): UserEntity {
        return if (aggregate.id == null) {
            aggregate.copy(id = UUID.randomUUID())
        } else {
            aggregate
        }
    }
}
