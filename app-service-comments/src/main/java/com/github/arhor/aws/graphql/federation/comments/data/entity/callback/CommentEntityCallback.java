package com.github.arhor.aws.graphql.federation.comments.data.entity.callback;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommentEntityCallback implements BeforeConvertCallback<CommentEntity> {

    @Nonnull
    @Override
    public CommentEntity onBeforeConvert(@Nonnull final CommentEntity aggregate) {
        return (aggregate.id() != null) ? aggregate : aggregate.toBuilder()
            .id(UUID.randomUUID())
            .build();
    }
}
