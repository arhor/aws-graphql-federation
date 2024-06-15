package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class StateGuard {

    private final PostRepresentationRepository postRepository;
    private final UserRepresentationRepository userRepository;

    void ensureCommentsEnabled(
        @Nonnull final String parentEntity,
        @Nonnull final Operation operation,
        @Nonnull final Type type,
        @Nonnull final UUID id
    ) {
        final var commentableRepository = switch (type) {
            case USER -> userRepository;
            case POST -> postRepository;
        };
        final var commentable = commentableRepository
            .findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    parentEntity,
                    type.entity + " with " + type.field + " = " + id + " is not found",
                    operation
                )
            );

        if (commentable.commentsDisabled()) {
            throw new EntityOperationRestrictedException(
                parentEntity,
                "Comments disabled for the " + type.entity + " with " + type.field + " = " + id,
                operation
            );
        }
    }

    @Getter
    @RequiredArgsConstructor
    enum Type {
        USER(DgsConstants.USER.TYPE_NAME, DgsConstants.USER.Id),
        POST(DgsConstants.POST.TYPE_NAME, DgsConstants.POST.Id),
        ;
        @Nonnull
        private final String entity;
        @Nonnull
        private final String field;
    }
}
