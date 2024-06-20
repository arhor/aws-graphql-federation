package com.github.arhor.aws.graphql.federation.comments.data.entity;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Immutable;

@Immutable
public interface Commentable<T> {

    /**
     * Toggles comments functionality for a given entity. This method does not
     * modify state of an entity, instead a copy created with updated state.
     *
     * @return a copy of a given entity with updated state
     */
    @NotNull
    T toggleComments();

    /**
     * Indicates that comments functionality enabled or disabled for a given entity.
     *
     * @return {@code true} if comments disabled, otherwise {@code false}
     */
    boolean commentsDisabled();
}
