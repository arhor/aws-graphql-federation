package com.github.arhor.aws.graphql.federation.comments.data.entity.callback

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CommentEntityCallback : BeforeConvertCallback<CommentEntity> {

    override fun onBeforeConvert(aggregate: CommentEntity): CommentEntity {
        return if (aggregate.id == null) {
            aggregate.toBuilder()
                .id(UUID.randomUUID())
                .build()
        } else {
            aggregate
        }
    }
}
