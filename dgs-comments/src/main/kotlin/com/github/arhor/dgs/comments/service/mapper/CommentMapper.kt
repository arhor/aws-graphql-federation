package com.github.arhor.dgs.comments.service.mapper

import com.github.arhor.dgs.comments.data.entity.CommentEntity
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentInput
import com.github.arhor.dgs.lib.mapstruct.IgnoreAuditMappings
import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(config = MapstructCommonConfig::class)
interface CommentMapper {

    @IgnoreAuditMappings
    @Mapping(target = "id", ignore = true)
    fun mapToEntity(request: CreateCommentInput): CommentEntity

    fun mapToDTO(entity: CommentEntity): Comment
}
