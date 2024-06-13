package com.github.arhor.aws.graphql.federation.comments.data.entity;

import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
import jakarta.annotation.Nonnull;

public interface HasComments {

    @Nonnull
    Features<Feature> features();

    enum Feature {
        COMMENTS_DISABLED,
    }
}
