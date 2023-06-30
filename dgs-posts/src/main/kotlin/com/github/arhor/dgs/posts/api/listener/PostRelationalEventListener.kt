package com.github.arhor.dgs.posts.api.listener

import com.github.arhor.dgs.lib.HEADER_PAYLOAD_TYPE
import com.github.arhor.dgs.posts.config.props.AppProps
import com.github.arhor.dgs.posts.data.entity.PostEntity
import io.awspring.cloud.sns.core.SnsNotification
import io.awspring.cloud.sns.core.SnsOperations
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent
import org.springframework.messaging.MessagingException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
@Retryable(retryFor = [MessagingException::class])
class PostRelationalEventListener(
    private val snsOperations: SnsOperations,
    appProps: AppProps,
) : AbstractRelationalEventListener<PostEntity>() {

    private val postChangesTopic = appProps.aws.sns.postChanges

    override fun onAfterSave(event: AfterSaveEvent<PostEntity>) {
        sendNotification(
            payload = PostChange.Updated(id = event.entity.id!!),
            type = POST_CHANGE_UPDATED
        )
    }

    override fun onAfterDelete(event: AfterDeleteEvent<PostEntity>) {
        sendNotification(
            payload = PostChange.Deleted(id = event.id.value as Long),
            type = POST_CHANGE_DELETED
        )
    }

    private fun sendNotification(payload: PostChange, type: String) {
        snsOperations.sendNotification(
            postChangesTopic,
            SnsNotification(
                payload,
                mapOf(HEADER_PAYLOAD_TYPE to type)
            )
        )
    }

    companion object {
        private const val POST_CHANGE_UPDATED = "PostChange.Updated"
        private const val POST_CHANGE_DELETED = "PostChange.Deleted"
    }

    sealed interface PostChange {
        data class Updated(val id: Long) : PostChange
        data class Deleted(val id: Long) : PostChange
    }
}
