package com.github.arhor.dgs.topics.graphql.fetcher

import com.github.arhor.dgs.topics.generated.graphql.DgsConstants
import com.github.arhor.dgs.topics.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class UserFetcher {

    @DgsEntityFetcher(name = DgsConstants.USER.TYPE_NAME)
    fun user(values: Map<String, Any>): User {
        return User(id = values["id"]!!.toString())
    }
}
