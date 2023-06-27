package com.github.arhor.dgs.articles.api.graphql.datafetcher

import com.github.arhor.dgs.articles.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.articles.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher
import java.math.BigInteger

@DgsComponent
class EntityFetcher {

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun user(values: Map<String, Any>): User = User(id = (values[USER.Id] as BigInteger).longValueExact())
}
