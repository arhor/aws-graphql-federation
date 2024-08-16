package com.github.arhor.aws.graphql.federation.users.service.mapping

import com.github.arhor.aws.graphql.federation.starter.security.CurrentUser
import com.github.arhor.aws.graphql.federation.users.data.model.AuthEntity
import com.github.arhor.aws.graphql.federation.users.data.model.UserEntity
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User

interface UserMapper {
    fun mapToEntity(input: CreateUserInput, defaultAuth: AuthEntity): UserEntity
    fun mapToResult(entity: UserEntity): User
    fun mapToCurrentUser(user: UserEntity, authorities: Iterable<AuthEntity>): CurrentUser
}
