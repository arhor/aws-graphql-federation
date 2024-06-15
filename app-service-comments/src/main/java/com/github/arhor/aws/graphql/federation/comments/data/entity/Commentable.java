package com.github.arhor.aws.graphql.federation.comments.data.entity;

import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Immutable;

@Immutable
public interface Commentable<T> {

    Features<Feature> features();

    /**
     * Toggles comments functionality for a given entity. This method does not
     * modify state of an entity, instead a copy created with updated state.
     *
     * @return a copy of a given entity with updated state
     */
    @Nonnull
    T toggleComments();

    /**
     * Indicates that comments functionality enabled or disabled for a given entity.
     *
     * @return {@code true} if comments disabled, otherwise {@code false}
     */
    default boolean commentsDisabled() {
        return features().check(Feature.COMMENTS_DISABLED);
    }

    enum Feature {
        COMMENTS_DISABLED,
    }
}
