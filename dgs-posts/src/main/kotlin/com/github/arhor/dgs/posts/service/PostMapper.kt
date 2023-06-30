package com.github.arhor.dgs.posts.service

import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import com.github.arhor.dgs.posts.data.entity.PostEntity
import com.github.arhor.dgs.posts.data.entity.TagRef
import com.github.arhor.dgs.posts.data.entity.projection.PostProjection
import com.github.arhor.dgs.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.dgs.posts.generated.graphql.types.Post
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.util.Collections

@Mapper(
    config = MapstructCommonConfig::class,
    implementationPackage = "com.github.arhor.dgs.posts.generated.mapper",
    imports = [Collections::class]
)
interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "banner", expression = "java(banner)")
    @Mapping(target = "tags", expression = "java(tags)")
    fun mapToEntity(dto: CreatePostInput, banner: String?, tags: Set<TagRef>): PostEntity

    @Mapping(target = "tags", ignore = true)
    fun mapToDTO(entity: PostEntity): Post

    @Mapping(target = "tags", ignore = true)
    fun mapToDTO(projection: PostProjection): Post
}
