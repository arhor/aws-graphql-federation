package com.github.arhor.dgs.comments.api.graphql.datafetcher

import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.POST
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.comments.generated.graphql.types.Post
import com.github.arhor.dgs.comments.generated.graphql.types.User
import com.github.arhor.dgs.lib.getLong
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class FederatedEntityFetchers {

    /* Entity Fetchers */

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    fun fetchPost(values: Map<String, Any>): Post = Post(id = values.getLong(POST.Id))

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun fetchUser(values: Map<String, Any>): User = User(id = values.getLong(USER.Id))
}
