package com.github.arhor.dgs.posts.api.graphql.datafetcher

import com.github.arhor.dgs.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.posts.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher
import java.math.BigInteger

@DgsComponent
class UserFetcher {

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun user(values: Map<String, Any>): User = User(
        id = values[USER.Id].let {
            when (it) {
                is BigInteger -> it.longValueExact()
                else -> it.toString().toLong()
            }
        }
    )
}
