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

    private val userChangesTopic = appProps.aws.sns.userChanges

    override fun onAfterSave(event: AfterSaveEvent<UserEntity>) {
        sendNotification(
            payload = UserChange.Updated(id = event.entity.id!!),
            type = USER_CHANGE_UPDATED
        )
    }

    override fun onAfterDelete(event: AfterDeleteEvent<UserEntity>) {
        sendNotification(
            payload = UserChange.Deleted(id = event.id.value as Long),
            type = USER_CHANGE_DELETED
        )
    }

    private fun sendNotification(payload: UserChange, type: String) {
        snsOperations.sendNotification(
            userChangesTopic,
            SnsNotification(
                payload,
                mapOf(HEADER_PAYLOAD_TYPE to type)
            )
        )
    }

    companion object {
        private const val USER_CHANGE_UPDATED = "UserChange.Updated"
        private const val USER_CHANGE_DELETED = "UserChange.Deleted"
    }

    sealed interface UserChange {
        data class Updated(val id: Long) : UserChange
        data class Deleted(val id: Long) : UserChange
    }
}
