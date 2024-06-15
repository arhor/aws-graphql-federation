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

import java.util.UUID;

@Table("user_representations")
@Immutable
@Builder(toBuilder = true)
public record UserRepresentation(
    @Id
    @Column("id")
    UUID id,

    @Column("features")
    Features<Feature> features,

    @Transient
    boolean shouldBePersisted
) implements Persistable<UUID>, Commentable<UserRepresentation> {

    public UserRepresentation {
        if (features == null) {
            features = Features.emptyOf(Feature.class);
        }
    }

    @PersistenceCreator
    public UserRepresentation(final UUID id, final Features<Feature> features) {
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

    @Override
    public boolean commentsDisabled() {
        return features.check(Feature.COMMENTS_DISABLED);
    }

    @Nonnull
    @Override
    public UserRepresentation toggleComments() {
        return this.toBuilder()
            .features(this.features().toggle(Feature.COMMENTS_DISABLED))
            .build();
    }

    public enum Feature {
        COMMENTS_DISABLED,
    }
}
