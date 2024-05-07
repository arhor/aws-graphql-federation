package com.github.arhor.aws.graphql.federation.posts.util

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput

val PostsLookupInput.limit
    get() = size

val PostsLookupInput.offset
    get() = size * page
