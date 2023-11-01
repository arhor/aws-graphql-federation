package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher

import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.POST
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.comments.generated.graphql.types.Post
import com.github.arhor.dgs.comments.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.common.getLong
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class FederatedEntityFetchers {

    /* Entity Fetchers */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun resolveUser(values: Map<String, Any>) = User(id = values.getLong(USER.Id))

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    fun resolvePost(values: Map<String, Any>) = Post(id = values.getLong(POST.Id))
}
