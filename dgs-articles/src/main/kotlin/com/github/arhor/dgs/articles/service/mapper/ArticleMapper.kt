package com.github.arhor.dgs.articles.service.mapper

import com.github.arhor.dgs.articles.data.entity.ArticleEntity
import com.github.arhor.dgs.articles.generated.graphql.types.Article
import com.github.arhor.dgs.articles.generated.graphql.types.CreateArticleRequest
import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(config = MapstructCommonConfig::class)
interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    fun mapToEntity(request: CreateArticleRequest): ArticleEntity

    fun mapToDTO(it: ArticleEntity): Article
}
