package com.github.arhor.dgs.users.service.mapper

import com.github.arhor.dgs.users.common.IgnoreAuditMappings
import com.github.arhor.dgs.users.common.MapstructCommonConfig
import com.github.arhor.dgs.users.common.OptionalMapper
import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(
    config = MapstructCommonConfig::class,
    uses = [
        OptionalMapper::class,
        SettingsMapper::class,
    ]
)
interface UserMapper {

    @IgnoreAuditMappings
    @Mapping(target = "id", ignore = true)
    fun mapToEntity(request: CreateUserRequest): UserEntity

    fun mapToDTO(user: UserEntity): User
}
