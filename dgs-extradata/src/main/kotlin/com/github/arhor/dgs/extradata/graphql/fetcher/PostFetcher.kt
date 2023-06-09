package com.github.arhor.dgs.extradata.graphql.fetcher

import com.github.arhor.dgs.extradata.generated.graphql.DgsConstants.POST
import com.github.arhor.dgs.extradata.generated.graphql.types.Post
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class PostFetcher {

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    fun post(values: Map<String, Any>) = Post(id = values[POST.Id]!!.toString())
}
