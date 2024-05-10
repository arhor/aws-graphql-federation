package com.github.arhor.aws.graphql.federation.comments.util;

public enum Caches {
    /**
     * Cache for preparsed GraphQL documents.
     */
    GRAPHQL_DOCUMENTS,

    /**
     * Cache for idempotent operation execution.
     */
    IDEMPOTENT_ID_SET,
}
