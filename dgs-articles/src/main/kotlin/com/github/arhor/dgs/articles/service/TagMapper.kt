package com.github.arhor.dgs.articles.service

import com.github.arhor.dgs.articles.data.entity.TagEntity
import com.github.arhor.dgs.articles.generated.graphql.types.CreateTagInput
import com.github.arhor.dgs.articles.generated.graphql.types.Tag
import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(
    config = MapstructCommonConfig::class,
    implementationPackage = "com.github.arhor.dgs.articles.generated.mapper"
)
interface TagMapper {

    @Mapping(target = "id", ignore = true)
    fun mapToEntity(input: CreateTagInput): TagEntity

    fun mapToDTO(entity: TagEntity): Tag
}
