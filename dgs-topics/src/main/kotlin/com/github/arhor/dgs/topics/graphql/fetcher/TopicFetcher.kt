package com.github.arhor.dgs.topics.graphql.fetcher

import com.github.arhor.dgs.topics.generated.graphql.types.CreateTopicRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Topic
import com.github.arhor.dgs.topics.service.TopicService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class TopicFetcher(private val topicService: TopicService) {

    @DgsMutation
    fun createTopic(@InputArgument request: CreateTopicRequest): Topic {
        return topicService.createNewTopic(request)
    }

    @DgsQuery
    fun topics(@InputArgument limit: Int, @InputArgument offset: Int): List<Topic> {
        return topicService.getAllTopics(limit, offset)
    }
}
