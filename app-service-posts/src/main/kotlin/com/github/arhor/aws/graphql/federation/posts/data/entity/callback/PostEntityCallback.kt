package com.github.arhor.aws.graphql.federation.posts.data.entity.callback

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PostEntityCallback : BeforeConvertCallback<PostEntity> {

    override fun onBeforeConvert(aggregate: PostEntity): PostEntity {
        return if (aggregate.id == null) {
            aggregate.copy(id = UUID.randomUUID())
        } else {
            aggregate
        }
    }
}
