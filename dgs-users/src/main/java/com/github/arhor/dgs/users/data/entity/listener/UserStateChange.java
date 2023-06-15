package com.github.arhor.dgs.users.data.entity.listener;

import com.github.arhor.dgs.users.data.entity.UserEntity;

/**
 * Interface represents User entity state change.
 */
public sealed interface UserStateChange {

    /**
     * Concrete type of change.
     */
    String type();

    record Updated(Long id) implements UserStateChange {

        public Updated(final UserEntity user) {
            this(user.getId());
        }

        @Override
        public String type() {
            return "UserStateChange.Updated";
        }
    }

    record Deleted(Long id) implements UserStateChange {

        public Deleted(final UserEntity user) {
            this(user.getId());
        }

        @Override
        public String type() {
            return "UserStateChange.Deleted";
        }
    }
}
