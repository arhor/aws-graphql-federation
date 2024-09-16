package com.github.arhor.aws.graphql.federation.users.service.mapping.impl

import com.github.arhor.aws.graphql.federation.starter.security.CurrentUser
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.data.model.AuthEntity
import com.github.arhor.aws.graphql.federation.users.data.model.AuthRef
import com.github.arhor.aws.graphql.federation.users.data.model.UserEntity
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.service.mapping.UserMapper
import org.springframework.stereotype.Component

@Trace
@Component
class UserMapperImpl : UserMapper {

    override fun mapToEntity(input: CreateUserInput, auth: AuthEntity) = UserEntity(
        username = input.username,
        password = input.password,
        authorities = setOf(AuthRef.from(auth)),
    )

    override fun mapToResult(entity: UserEntity): User = User(
        id = entity.id ?: throw IllegalArgumentException("Entity must be persisted with assigned id!"),
        username = entity.username,
    )

    override fun mapToCurrentUser(user: UserEntity, authorities: Iterable<AuthEntity>): CurrentUser {
        return CurrentUser(
            id = user.id ?: throw IllegalArgumentException("Entity must be persisted with assigned id!"),
            authorities = authorities.map { it.name },
        )
    }
}
