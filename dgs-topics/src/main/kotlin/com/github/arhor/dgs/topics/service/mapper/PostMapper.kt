package com.github.arhor.dgs.topics.service.mapper

import com.github.arhor.dgs.lib.mapstruct.IgnoreAuditMappings
import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import com.github.arhor.dgs.topics.data.entity.PostEntity
import com.github.arhor.dgs.topics.generated.graphql.types.CreatePostRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Post
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(config = MapstructCommonConfig::class)
interface PostMapper {

    @IgnoreAuditMappings
    @Mapping(target = "id", ignore = true)
    fun mapToEntity(request: CreatePostRequest): PostEntity

    fun mapToDTO(user: PostEntity): Post
}
