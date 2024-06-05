package com.github.arhor.aws.graphql.federation.comments.data.entity;

import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("post_representations")
@Immutable
@Builder(toBuilder = true)
public record PostRepresentation(
    @Id
    @Column("id")
    UUID id,

    @Column("features")
    Features<Feature> features,

    @Transient
    boolean shouldBePersisted
) implements Persistable<UUID>, HasComments {

    public PostRepresentation {
        if (features == null) {
            features = Features.emptyOf(Feature.class);
        }
    }

    @PersistenceCreator
    public PostRepresentation(final UUID id, final Features<Feature> features) {
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
}
