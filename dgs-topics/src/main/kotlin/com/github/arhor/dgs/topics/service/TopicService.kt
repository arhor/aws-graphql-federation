package com.github.arhor.dgs.topics.service

import com.github.arhor.dgs.topics.generated.graphql.types.CreateTopicRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Topic

interface TopicService {
    fun createNewTopic(request: CreateTopicRequest): Topic
    fun getAllTopics(limit: Int, offset: Int): List<Topic>
}
