package com.github.arhor.dgs.articles.service

import com.github.arhor.dgs.articles.data.entity.ArticleEntity
import com.github.arhor.dgs.articles.data.entity.projection.ArticleProjection
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleInput
import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(
    config = MapstructCommonConfig::class,
    implementationPackage = "com.github.arhor.dgs.articles.generated.mapper"
)
interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "banner", ignore = true)
    @Mapping(target = "tags", expression = "java(java.util.Collections.emptySet())")
    @Mapping(target = "withTags", ignore = true)
    fun mapToEntity(input: CreateArticleInput): ArticleEntity

    @Mapping(target = "tags", ignore = true)
    fun mapToDTO(entity: ArticleEntity): Article

    @Mapping(target = "tags", ignore = true)
    fun mapToDTO(projection: ArticleProjection): Article
}
