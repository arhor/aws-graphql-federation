package com.github.arhor.dgs.users.data.listener;

import com.github.arhor.dgs.users.data.entity.UserEntity;
import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsOperations;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.messaging.MessagingException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Retryable(retryFor = MessagingException.class)
public class UserStateChangedEventListener extends AbstractRelationalEventListener<UserEntity> {

    public static final String USER_UPDATED_EVENTS_PROP = "application-props.aws.sns.user-updated-events";
    public static final String USER_DELETED_EVENTS_PROP = "application-props.aws.sns.user-deleted-events";

    private static final String HEADER_PAYLOAD_TYPE = "xPayloadType";

    private final SnsOperations snsOperations;
    private final String userUpdatedEventsTopic;
    private final String userDeletedEventsTopic;

    @Autowired
    public UserStateChangedEventListener(
        final SnsOperations snsOperations,
        @Value("${" + USER_UPDATED_EVENTS_PROP + ":#{null}}") final String userUpdatedEventsTopic,
        @Value("${" + USER_DELETED_EVENTS_PROP + ":#{null}}") final String userDeletedEventsTopic
    ) {
        final var updatedDestMissing = userUpdatedEventsTopic == null;
        final var deletedDestMissing = userDeletedEventsTopic == null;

        if (updatedDestMissing || deletedDestMissing) {
            final var error = new MissingRequiredPropertiesException();
            final var props = error.getMissingRequiredProperties();

            if (updatedDestMissing) {
                props.add(USER_UPDATED_EVENTS_PROP);
            }
            if (deletedDestMissing) {
                props.add(USER_DELETED_EVENTS_PROP);
            }
            throw error;
        }
        this.snsOperations = snsOperations;
        this.userUpdatedEventsTopic = userUpdatedEventsTopic;
        this.userDeletedEventsTopic = userDeletedEventsTopic;
    }

    @Override
    public void onAfterSave(@Nonnull final AfterSaveEvent<UserEntity> event) {
        sendNotification(
            userUpdatedEventsTopic,
            new UserStateChange.Updated(event)
        );
    }

    @Override
    public void onAfterDelete(@Nonnull final AfterDeleteEvent<UserEntity> event) {
        sendNotification(
            userDeletedEventsTopic,
            new UserStateChange.Deleted(event)
        );
    }

    private void sendNotification(final String destination, final UserStateChange payload) {
        snsOperations.sendNotification(
            destination,
            new SnsNotification<>(
                payload,
                Map.of(HEADER_PAYLOAD_TYPE, payload.type())
            )
        );
    }
}
