package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserService
import com.github.arhor.aws.graphql.federation.tracing.Trace
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    override fun findInternalUserRepresentation(userId: UUID): User {
        return userRepository.findByIdOrNull(userId)?.let(::mapEntityToUser)
            ?: throw EntityNotFoundException(
                DgsConstants.USER.TYPE_NAME,
                "${DgsConstants.USER.Id} = $userId",
                Operation.LOOKUP,
            )
    }

    override fun createInternalUserRepresentation(userId: UUID, idempotencyId: UUID) {
        val entities = UserEntity(id = userId)

        userRepository.save(entities)
    }

    override fun deleteInternalUserRepresentation(userId: UUID, idempotencyId: UUID) {
        userRepository.deleteById(userId)
    }

    private fun mapEntityToUser(entity: UserEntity): User {
        return User(
            id = entity.id,
        )
    }
}
