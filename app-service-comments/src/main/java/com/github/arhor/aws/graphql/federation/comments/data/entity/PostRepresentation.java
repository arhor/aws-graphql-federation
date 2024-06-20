package com.github.arhor.aws.graphql.federation.comments.data.entity;

import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.EnumSet;
import java.util.UUID;

@Table("post_representations")
@Immutable
@Builder(toBuilder = true)
public record PostRepresentation(
    @Id
    @Column("id")
    UUID id,

    @Column("user_id")
    UUID userId,

    @Column("features")
    PostFeatures features,

    @Transient
    boolean shouldBePersisted
) implements Persistable<UUID>, Commentable<PostRepresentation> {

    public PostRepresentation {
        if (features == null) {
            features = new PostFeatures();
        }
    }

    @PersistenceCreator
    public PostRepresentation(final UUID id, final UUID userId, final PostFeatures features) {
        this(id, userId, features, false);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return shouldBePersisted;
    }

    @NotNull
    @Override
    public PostRepresentation toggleComments() {
        return toBuilder()
            .features(features.toggle(PostFeature.COMMENTS_DISABLED))
            .build();
    }

    @Override
    public boolean commentsDisabled() {
        return features().check(PostFeature.COMMENTS_DISABLED);
    }

    public enum PostFeature {
        COMMENTS_DISABLED,
    }

    public static final class PostFeatures extends Features<PostFeatures, PostFeature> {

        public PostFeatures() {
            super(EnumSet.noneOf(PostFeature.class));
        }

        public PostFeatures(final PostFeature feature, final PostFeature... features) {
            super(EnumSet.of(feature, features));
        }


        public PostFeatures(@NotNull final EnumSet<PostFeature> items) {
            super(EnumSet.copyOf(items));
        }

        @NotNull
        @Override
        protected PostFeatures create(@NotNull final EnumSet<PostFeature> items) {
            return new PostFeatures(items);
        }
    }
}
