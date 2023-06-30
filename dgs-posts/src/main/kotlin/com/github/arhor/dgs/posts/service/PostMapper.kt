package com.github.arhor.dgs.posts.service

import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import com.github.arhor.dgs.posts.data.entity.PostEntity
import com.github.arhor.dgs.posts.data.entity.TagRef
import com.github.arhor.dgs.posts.data.entity.projection.PostProjection
import com.github.arhor.dgs.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.dgs.posts.generated.graphql.types.Option
import com.github.arhor.dgs.posts.generated.graphql.types.Post
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.util.Collections
import java.util.EnumSet

@Mapper(
    config = MapstructCommonConfig::class,
    implementationPackage = "com.github.arhor.dgs.posts.generated.mapper",
    imports = [Collections::class]
)
abstract class PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "updatedDateTime", ignore = true)
    @Mapping(target = "banner", expression = "java(banner)")
    @Mapping(target = "tags", expression = "java(tags)")
    abstract fun mapToEntity(dto: CreatePostInput, banner: String?, tags: Set<TagRef>): PostEntity

    @Mapping(target = "options", source = "options.items")
    @Mapping(target = "tags", ignore = true)
    abstract fun mapToDTO(entity: PostEntity): Post

    @Mapping(target = "options", source = "options.items")
    @Mapping(target = "tags", ignore = true)
    abstract fun mapToDTO(projection: PostProjection): Post

    protected fun wrap(options: List<Option>): PostEntity.Options {
        return PostEntity.Options(items = EnumSet.noneOf(Option::class.java).apply { addAll(options) })
    }
}
