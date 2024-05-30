package com.github.arhor.aws.graphql.federation.comments.data.entity;

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

    @Column("comments_disabled")
    boolean commentsDisabled,

    @Transient
    boolean shouldBePersisted
) implements Persistable<UUID>, HasComments {

    @PersistenceCreator
    public PostRepresentation(UUID id, boolean commentsDisabled) {
        this(id, commentsDisabled, false);
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
