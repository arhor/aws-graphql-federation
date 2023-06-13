package com.github.arhor.dgs.users.data.entity.listener;

import com.github.arhor.dgs.users.data.entity.UserEntity;
import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsOperations;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.data.relational.core.mapping.event.RelationalEvent;
import org.springframework.messaging.MessagingException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
@Retryable(retryFor = MessagingException.class, maxAttemptsExpression = "${application-props.retry-attempts:3}")
public class UserStateChangedEventListener extends AbstractRelationalEventListener<UserEntity> {

    private static final String HEADER_PAYLOAD_TYPE = "xPayloadType";

    private final SnsOperations messenger;
    private final String userUpdatedTopicName;
    private final String userDeletedTopicName;

    public UserStateChangedEventListener(
        final SnsOperations messenger,
        @Value("application-props.aws.user-updated-topic") final String userUpdatedTopicName,
        @Value("application-props.aws.user-deleted-topic") final String userDeletedTopicName
    ) {
        this.messenger = messenger;
        this.userUpdatedTopicName = userUpdatedTopicName;
        this.userDeletedTopicName = userDeletedTopicName;
    }

    @Override
    public void onAfterSave(@Nonnull final AfterSaveEvent<UserEntity> event) {
        sendNotification(event, UserStateChange.Updated::new, userUpdatedTopicName);
    }

    @Override
    public void onAfterDelete(@Nonnull final AfterDeleteEvent<UserEntity> event) {
        sendNotification(event, UserStateChange.Deleted::new, userDeletedTopicName);
    }

    private void sendNotification(
        final RelationalEvent<UserEntity> event,
        final Function<UserEntity, UserStateChange> stateChangeExtractor,
        final String destination
    ) {
        final var stateChanged = stateChangeExtractor.apply(event.getEntity());
        final var notification = new SnsNotification<>(stateChanged, Map.of(HEADER_PAYLOAD_TYPE, stateChanged.type()));

        messenger.sendNotification(destination, notification);
    }
}
