package com.github.arhor.dgs.comments.api.graphql.fetcher

import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.POST
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.comments.generated.graphql.types.Post
import com.github.arhor.dgs.comments.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher
import java.math.BigDecimal
import java.math.BigInteger

@DgsComponent
class FederatedEntityFetchers {

    /* Entity Fetchers */

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    fun fetchPost(values: Map<String, Any>): Post =
        Post(id = convertId(values[POST.Id]))

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun fetchUser(values: Map<String, Any>): User =
        User(id = convertId(values[USER.Id]))

    /* Internal implementation */

    private fun convertId(id: Any?): Long {
        return when (id) {
            is BigInteger -> id.longValueExact()
            is BigDecimal -> id.longValueExact()
            is Number -> id.toLong()
            null -> throw IllegalArgumentException("Field 'id' must not be null!")
            else -> id.toString().toLong()
        }
    }
}
