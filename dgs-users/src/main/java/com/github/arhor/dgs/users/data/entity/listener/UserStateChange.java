package com.github.arhor.dgs.users.data.entity.listener;

import com.github.arhor.dgs.users.data.entity.UserEntity;

public sealed interface UserStateChange {

    String type();

    record Updated(String userId) implements UserStateChange {

        public Updated(final UserEntity user) {
            this(user.getId().toString());
        }

        @Override
        public String type() {
            return "UserStateChangedEvent.Updated";
        }
    }

    record Deleted(String userId) implements UserStateChange {

        public Deleted(final UserEntity user) {
            this(user.getId().toString());
        }

        @Override
        public String type() {
            return "UserStateChangedEvent.Deleted";
        }
    }
}
