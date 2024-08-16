package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.data.repository.UserRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class StateGuard {

    private final PostRepresentationRepository postRepository;
    private final UserRepresentationRepository userRepository;

    void ensureCommentsEnabled(
        @NotNull final String parentEntity,
        @NotNull final Operation operation,
        @NotNull final Type type,
        @NotNull final UUID id
    ) {
        final var repository = switch (type) {
            case USER -> userRepository;
            case POST -> postRepository;
        };
        final var entity = repository.findById(id).orElseThrow(() ->
            new EntityNotFoundException(
                parentEntity,
                type.entity + " with " + type.field + " = " + id + " is not found",
                operation
            )
        );
        if (entity.commentsDisabled()) {
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
        @NotNull
        private final String entity;
        @NotNull
        private final String field;
    }
}
