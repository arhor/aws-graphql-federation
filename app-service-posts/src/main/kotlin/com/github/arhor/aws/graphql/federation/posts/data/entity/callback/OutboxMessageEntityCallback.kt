package com.github.arhor.aws.graphql.federation.posts.data.entity.callback

import com.github.arhor.aws.graphql.federation.posts.data.entity.OutboxMessageEntity
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OutboxMessageEntityCallback : BeforeConvertCallback<OutboxMessageEntity> {

    override fun onBeforeConvert(aggregate: OutboxMessageEntity): OutboxMessageEntity {
        return if (aggregate.id == null) {
            aggregate.copy(id = UUID.randomUUID())
        } else {
            aggregate
        }
    }
}
