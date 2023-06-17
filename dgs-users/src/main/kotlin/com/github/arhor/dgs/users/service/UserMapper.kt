package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.lib.mapstruct.IgnoreAuditMappings
import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import com.github.arhor.dgs.lib.mapstruct.OptionalMapper
import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(
    config = MapstructCommonConfig::class,
    uses = [OptionalMapper::class],
)
interface UserMapper {

    @IgnoreAuditMappings
    @Mapping(target = "id", ignore = true)
    fun mapToEntity(request: CreateUserRequest): UserEntity

    fun mapToDTO(user: UserEntity): User
}
