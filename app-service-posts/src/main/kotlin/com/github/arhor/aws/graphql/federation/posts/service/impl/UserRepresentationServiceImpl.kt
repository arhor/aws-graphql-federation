package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.model.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.collections.set

@Trace
@Service
class UserRepresentationServiceImpl(
    private val userRepository: UserRepresentationRepository,
) : UserRepresentationService {

    override fun findUsersRepresentationsInBatch(userIds: Set<UUID>): Map<UUID, User> {
        val result = HashMap<UUID, User>(userIds.size)
        val users = userRepository.findAllById(userIds)

        for (user in users) {
            result[user.id] = User(
                id = user.id,
                postsDisabled = user.postsDisabled(),
            )
        }
        userIds.filter { it !in result.keys }.forEach {
            result[it] = User(id = it)
        }
        return result
    }

    @Cacheable
    override fun createUserRepresentation(userId: UUID) {
        userRepository.save(
            UserRepresentation(
                id = userId,
                shouldBePersisted = true,
            )
        )
    }

    @Cacheable
    override fun deleteUserRepresentation(userId: UUID) {
        userRepository.deleteById(userId)
    }

    @Transactional
    override fun toggleUserPosts(userId: UUID): Boolean {
        val presentUser =
            userRepository.findByIdOrNull(userId)
                ?: throw EntityNotFoundException(
                    USER.TYPE_NAME,
                    "${USER.Id} = $userId",
                    Operation.UPDATE
                )

        val updatedUser = userRepository.save(presentUser.togglePosts())

        return !updatedUser.postsDisabled()
    }
}
