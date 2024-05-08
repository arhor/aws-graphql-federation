package com.github.arhor.aws.graphql.federation.posts.data.entity.callback

import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TagEntityCallback : BeforeConvertCallback<TagEntity> {

    override fun onBeforeConvert(aggregate: TagEntity): TagEntity {
        return if (aggregate.id == null) {
            aggregate.copy(id = UUID.randomUUID())
        } else {
            aggregate
        }
    }
}
