package com.github.arhor.dgs.users.api.listener

import com.github.arhor.dgs.lib.HEADER_PAYLOAD_TYPE
import com.github.arhor.dgs.users.config.props.AppProps
import com.github.arhor.dgs.users.data.entity.UserEntity
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
class UserRelationalEventListener(
    private val snsOperations: SnsOperations,
    appProps: AppProps,
) : AbstractRelationalEventListener<UserEntity>() {

    private val userStateChangesTopic = appProps.aws.sns.userStateChanges

    override fun onAfterSave(event: AfterSaveEvent<UserEntity>) {
        sendNotification(
            payload = UserStateChange.Updated(
                id = event.entity.id!!
            )
        )
    }

    override fun onAfterDelete(event: AfterDeleteEvent<UserEntity>) {
        sendNotification(
            payload = UserStateChange.Deleted(
                id = event.id.value as Long
            )
        )
    }

    private fun sendNotification(payload: UserStateChange) {
        snsOperations.sendNotification(
            userStateChangesTopic,
            SnsNotification(
                payload,
                mapOf(HEADER_PAYLOAD_TYPE to payload.type)
            )
        )
    }

    sealed class UserStateChange(val type: String) {
        data class Updated(val id: Long) : UserStateChange("UserStateChange.Updated")
        data class Deleted(val id: Long) : UserStateChange("UserStateChange.Deleted")
    }
}
