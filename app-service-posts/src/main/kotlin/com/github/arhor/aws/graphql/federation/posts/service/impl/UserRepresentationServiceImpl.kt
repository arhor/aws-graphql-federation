package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.model.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import org.slf4j.LoggerFactory
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
        if (userIds.isEmpty()) {
            return emptyMap()
        }
        val result =
            userRepository
                .findAllById(userIds)
                .associateByTo(
                    destination = HashMap(userIds.size),
                    keySelector = { it.id },
                    valueTransform = { User(id = it.id) },
                )

        (userIds - result.keys).takeIf(Set<UUID>::isNotEmpty)?.let {
            for (userId in it) {
                result[userId] = User(id = userId)
            }
            logger.warn("Stubbed users without representation in the DB: {}", it)
        }
        return result
    }

    @Cacheable(cacheNames = ["create-user-representation-requests-cache"])
    override fun createUserRepresentation(userId: UUID, idempotencyKey: UUID) {
        userRepository.save(
            UserRepresentation(
                id = userId,
                shouldBePersisted = true,
            )
        )
    }

    @Cacheable(cacheNames = ["delete-user-representation-requests-cache"])
    override fun deleteUserRepresentation(userId: UUID, idempotencyKey: UUID) {
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

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
