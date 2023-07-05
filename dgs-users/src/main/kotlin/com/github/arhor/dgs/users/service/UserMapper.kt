package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.User

interface UserMapper {
    fun mapToEntity(input: CreateUserInput): UserEntity
    fun mapToDTO(entity: UserEntity): User
}
