package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.getLong
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class FederatedEntityFetcher {

    /* Entity Fetchers */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun resolveUser(values: Map<String, Any>) = User(id = values.getLong(USER.Id))
}
