package com.github.arhor.dgs.extradata.graphql.fetcher

import com.github.arhor.dgs.extradata.generated.graphql.DgsConstants
import com.github.arhor.dgs.extradata.generated.graphql.types.Topic
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class TopicFetcher {

    @DgsEntityFetcher(name = DgsConstants.TOPIC.TYPE_NAME)
    fun topic(values: Map<String, Any>) = Topic(id = values[DgsConstants.TOPIC.Id]!!.toString())
}
