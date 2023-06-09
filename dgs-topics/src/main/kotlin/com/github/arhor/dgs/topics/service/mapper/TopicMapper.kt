package com.github.arhor.dgs.topics.service.mapper

import com.github.arhor.dgs.topics.common.MapstructCommonConfig
import com.github.arhor.dgs.topics.data.entity.TopicEntity
import com.github.arhor.dgs.topics.generated.graphql.types.CreateTopicRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Topic
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(config = MapstructCommonConfig::class)
interface TopicMapper {

    @Mapping(target = "id", ignore = true)
    fun mapToEntity(request: CreateTopicRequest): TopicEntity

    @Mapping(target = "posts", ignore = true)
    fun mapToDTO(it: TopicEntity): Topic
}
