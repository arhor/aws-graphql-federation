package com.github.arhor.aws.graphql.federation.comments.data.model.callback;

import com.github.arhor.aws.graphql.federation.comments.data.model.CommentEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommentEntityCallback implements BeforeConvertCallback<CommentEntity> {

    @NotNull
    @Override
    public CommentEntity onBeforeConvert(@NotNull final CommentEntity aggregate) {
        return (aggregate.id() != null) ? aggregate : aggregate.toBuilder()
            .id(UUID.randomUUID())
            .build();
    }
}
