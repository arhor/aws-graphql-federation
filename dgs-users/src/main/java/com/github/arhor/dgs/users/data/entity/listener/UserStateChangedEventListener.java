package com.github.arhor.dgs.users.data.entity.listener;

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
import org.springframework.data.relational.core.mapping.event.RelationalEvent;
import org.springframework.messaging.MessagingException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
@Retryable(retryFor = MessagingException.class)
public class UserStateChangedEventListener extends AbstractRelationalEventListener<UserEntity> {

    public static final String USER_UPDATED_EVENTS = "application-props.aws.sns.user-updated-events";
    public static final String USER_DELETED_EVENTS = "application-props.aws.sns.user-deleted-events";

    private static final String HEADER_PAYLOAD_TYPE = "xPayloadType";

    private final SnsOperations snsOperations;
    private final String userUpdatedEventsDestination;
    private final String userDeletedEventsDestination;

    @Autowired
    public UserStateChangedEventListener(
        final SnsOperations snsOperations,
        @Value("${" + USER_UPDATED_EVENTS + ":#{null}}") final String userUpdatedEventsDestination,
        @Value("${" + USER_DELETED_EVENTS + ":#{null}}") final String userDeletedEventsDestination
    ) {
        final var updatedDestMissing = userUpdatedEventsDestination == null;
        final var deletedDestMissing = userDeletedEventsDestination == null;

        if (updatedDestMissing || deletedDestMissing) {
            final var error = new MissingRequiredPropertiesException();
            final var props = error.getMissingRequiredProperties();

            if (updatedDestMissing) {
                props.add(USER_UPDATED_EVENTS);
            }
            if (deletedDestMissing) {
                props.add(USER_DELETED_EVENTS);
            }
            throw error;
        }
        this.snsOperations = snsOperations;
        this.userUpdatedEventsDestination = userUpdatedEventsDestination;
        this.userDeletedEventsDestination = userDeletedEventsDestination;
    }

    @Override
    public void onAfterSave(@Nonnull final AfterSaveEvent<UserEntity> event) {
        sendNotification(event, UserStateChange.Updated::new, userUpdatedEventsDestination);
    }

    @Override
    public void onAfterDelete(@Nonnull final AfterDeleteEvent<UserEntity> event) {
        sendNotification(event, UserStateChange.Deleted::new, userDeletedEventsDestination);
    }

    private void sendNotification(
        final RelationalEvent<UserEntity> event,
        final Function<UserEntity, UserStateChange> stateChangeExtractor,
        final String destination
    ) {
        final var changedState = stateChangeExtractor.apply(event.getEntity());
        final var notification = new SnsNotification<>(changedState, Map.of(HEADER_PAYLOAD_TYPE, changedState.type()));

        snsOperations.sendNotification(destination, notification);
    }
}
