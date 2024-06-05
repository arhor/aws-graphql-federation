package com.github.arhor.aws.graphql.federation.comments.data.entity;

import com.github.arhor.aws.graphql.federation.starter.core.data.Features;

public interface HasComments {

    Features<Feature> features();

    enum Feature {
        COMMENTS_DISABLED,
    }
}
