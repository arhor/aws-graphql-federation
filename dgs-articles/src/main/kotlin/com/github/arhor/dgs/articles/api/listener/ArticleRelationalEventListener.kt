package com.github.arhor.dgs.articles.api.listener

import com.github.arhor.dgs.articles.config.props.AppProps
import com.github.arhor.dgs.articles.data.entity.ArticleEntity
import com.github.arhor.dgs.lib.HEADER_PAYLOAD_TYPE
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
class ArticleRelationalEventListener(
    private val snsOperations: SnsOperations,
    appProps: AppProps,
) : AbstractRelationalEventListener<ArticleEntity>() {

    private val articleStateChangesTopic = appProps.aws.sns.articleStateChanges

    override fun onAfterSave(event: AfterSaveEvent<ArticleEntity>) {
        sendNotification(
            payload = ArticleStateChange.Updated(
                id = event.entity.id!!
            )
        )
    }

    override fun onAfterDelete(event: AfterDeleteEvent<ArticleEntity>) {
        sendNotification(
            payload = ArticleStateChange.Deleted(
                id = event.id.value as Long
            )
        )
    }

    private fun sendNotification(payload: ArticleStateChange) {
        snsOperations.sendNotification(
            articleStateChangesTopic,
            SnsNotification(
                payload,
                mapOf(HEADER_PAYLOAD_TYPE to payload.type)
            )
        )
    }

    sealed class ArticleStateChange(val type: String) {
        data class Updated(val id: Long) : ArticleStateChange(UPDATED)
        data class Deleted(val id: Long) : ArticleStateChange(DELETED)

        companion object {
            private val UPDATED = "${ArticleStateChange::class.simpleName}.${Updated::class.simpleName}"
            private val DELETED = "${ArticleStateChange::class.simpleName}.${Deleted::class.simpleName}"
        }
    }
}
