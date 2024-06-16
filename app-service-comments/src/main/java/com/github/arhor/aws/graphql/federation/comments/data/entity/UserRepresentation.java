package com.github.arhor.aws.graphql.federation.comments.data.entity;

import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.EnumSet;
import java.util.UUID;

@Table("user_representations")
@Immutable
@Builder(toBuilder = true)
public record UserRepresentation(
    @Id
    @Column("id")
    UUID id,

    @Column("features")
    UserFeatures features,

    @Transient
    boolean shouldBePersisted
) implements Persistable<UUID>, Commentable<UserRepresentation> {

    public UserRepresentation {
        if (features == null) {
            features = new UserFeatures();
        }
    }

    @PersistenceCreator
    public UserRepresentation(final UUID id, final UserFeatures features) {
        this(id, features, false);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return shouldBePersisted;
    }

    @Nonnull
    @Override
    public UserRepresentation toggleComments() {
        return toBuilder()
            .features(features.toggle(UserFeature.COMMENTS_DISABLED))
            .build();
    }

    @Override
    public boolean commentsDisabled() {
        return features().check(UserFeature.COMMENTS_DISABLED);
    }

    public enum UserFeature {
        COMMENTS_DISABLED,
    }

    public static final class UserFeatures extends Features<UserFeatures, UserFeature> {

        public UserFeatures() {
            super(EnumSet.noneOf(UserFeature.class));
        }

        public UserFeatures(@Nonnull final EnumSet<UserFeature> items) {
            super(EnumSet.copyOf(items));
        }

        @Nonnull
        @Override
        protected UserFeatures create(@Nonnull final EnumSet<UserFeature> items) {
            return new UserFeatures(items);
        }
    }
}
