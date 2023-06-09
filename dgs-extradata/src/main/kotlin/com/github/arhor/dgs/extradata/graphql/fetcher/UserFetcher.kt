package com.github.arhor.dgs.extradata.graphql.fetcher

import com.github.arhor.dgs.extradata.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.extradata.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class UserFetcher {

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun user(values: Map<String, Any>) = User(id = values[USER.Id]!!.toString())
}
