package com.github.arhor.dgs.users.api.listener;

import com.github.arhor.dgs.users.config.props.AppProps;
import com.github.arhor.dgs.users.data.entity.UserEntity;
import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsOperations;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.messaging.MessagingException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Retryable(retryFor = MessagingException.class)
public class UserRelationalEventListener extends AbstractRelationalEventListener<UserEntity> {

    private static final String HEADER_PAYLOAD_TYPE = "xPayloadType";

    private final SnsOperations snsOperations;
    private final String userUpdatedEventsTopic;
    private final String userDeletedEventsTopic;

    @Autowired
    public UserRelationalEventListener(final SnsOperations snsOperations, final AppProps appProps) {
        this.snsOperations = snsOperations;
        this.userUpdatedEventsTopic = appProps.getAws().getSns().getUserUpdatedEvents();
        this.userDeletedEventsTopic = appProps.getAws().getSns().getUserDeletedEvents();
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void onAfterSave(@Nonnull final AfterSaveEvent<UserEntity> event) {
        sendNotification(
            userUpdatedEventsTopic,
            new UserStateChange.Updated(event.getEntity().getId())
        );
    }

    @Override
    public void onAfterDelete(@Nonnull final AfterDeleteEvent<UserEntity> event) {
        sendNotification(
            userDeletedEventsTopic,
            new UserStateChange.Deleted((Long) event.getId().getValue())
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

    sealed interface UserStateChange {

        String type();

        record Updated(Long id) implements UserStateChange {
            @Override
            public String type() {
                return "UserStateChange.Updated";
            }
        }

        record Deleted(Long id) implements UserStateChange {
            @Override
            public String type() {
                return "UserStateChange.Deleted";
            }
        }
    }
}
