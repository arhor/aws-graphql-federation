package com.github.arhor.dgs.articles.service

import com.github.arhor.dgs.articles.data.entity.ArticleEntity
import com.github.arhor.dgs.articles.data.entity.TagRef
import com.github.arhor.dgs.articles.data.entity.projection.ArticleProjection
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.util.Collections

@Mapper(
    config = MapstructCommonConfig::class,
    implementationPackage = "com.github.arhor.dgs.articles.generated.mapper",
    imports = [Collections::class]
)
interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "banner", ignore = true)
    @Mapping(target = "tags", expression = "java(tags)")
    fun mapToEntity(dto: CreateArticleInput, tags: Set<TagRef>): ArticleEntity

    @Mapping(target = "tags", ignore = true)
    fun mapToDTO(entity: ArticleEntity): Article

    @Mapping(target = "tags", ignore = true)
    fun mapToDTO(projection: ArticleProjection): Article
}
