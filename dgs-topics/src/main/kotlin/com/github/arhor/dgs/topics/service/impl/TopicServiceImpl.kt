package com.github.arhor.dgs.topics.service.impl

import com.github.arhor.dgs.topics.common.Limit
import com.github.arhor.dgs.topics.common.Offset
import com.github.arhor.dgs.topics.common.OffsetBasedPageRequest
import com.github.arhor.dgs.topics.data.repository.TopicRepository
import com.github.arhor.dgs.topics.generated.graphql.types.CreateTopicRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Topic
import com.github.arhor.dgs.topics.service.TopicService
import com.github.arhor.dgs.topics.service.mapper.TopicMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicServiceImpl(
    private val topicRepository: TopicRepository,
    private val topicMapper: TopicMapper,
) : TopicService {

    @Transactional
    override fun createNewTopic(request: CreateTopicRequest): Topic {
        return topicMapper.mapToEntity(request)
            .let(topicRepository::save)
            .let(topicMapper::mapToDTO)
    }

    @Transactional(readOnly = true)
    override fun getAllTopics(offset: Offset, limit: Limit): List<Topic> {
        return topicRepository
            .findAll(OffsetBasedPageRequest(offset, limit))
            .map(topicMapper::mapToDTO)
            .toList()
    }
}
