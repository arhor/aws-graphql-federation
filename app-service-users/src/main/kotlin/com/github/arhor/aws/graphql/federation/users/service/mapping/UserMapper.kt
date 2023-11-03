package com.github.arhor.aws.graphql.federation.users.service.mapping

import com.github.arhor.aws.graphql.federation.users.data.entity.UserEntity
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User

interface UserMapper {
    fun mapToEntity(input: CreateUserInput): UserEntity
    fun mapToResult(entity: UserEntity): User
}
