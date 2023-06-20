package com.github.arhor.dgs.users.data.listener;

import com.github.arhor.dgs.users.data.entity.UserEntity;
import org.springframework.data.relational.core.mapping.event.RelationalDeleteEvent;
import org.springframework.data.relational.core.mapping.event.RelationalSaveEvent;

/**
 * Interface represents User entity state change.
 */
public sealed interface UserStateChange {

    /**
     * Concrete type of change.
     */
    String type();

    record Updated(Long id) implements UserStateChange {

        public Updated(final RelationalSaveEvent<UserEntity> user) {
            this(user.getEntity().getId());
        }

        @Override
        public String type() {
            return "UserStateChange.Updated";
        }
    }

    record Deleted(Long id) implements UserStateChange {

        public Deleted(final RelationalDeleteEvent<UserEntity> event) {
            this((Long) event.getId().getValue());
        }

        @Override
        public String type() {
            return "UserStateChange.Deleted";
        }
    }
}
