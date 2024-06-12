package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation.Feature
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.posts.util.Caches
import com.github.arhor.aws.graphql.federation.posts.util.get
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import jakarta.annotation.PostConstruct
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Trace
@Service
class UserRepresentationServiceImpl(
    private val cacheManager: CacheManager,
    private val userRepository: UserRepresentationRepository,
) : UserRepresentationService {

    private lateinit var cache: Cache

    @PostConstruct
    fun initialize() {
        cache = cacheManager[Caches.IDEMPOTENT_ID_SET]
    }

    override fun findUsersRepresentationsInBatch(userIds: Set<UUID>): Map<UUID, User> {
        val result = HashMap<UUID, User>(userIds.size)
        val users = userRepository.findAllById(userIds)

        for (user in users) {
            result[user.id] = User(
                id = user.id,
                postsDisabled = user.features.check(Feature.POSTS_DISABLED),
            )
        }
        userIds.filter { it !in result.keys }.forEach {
            result[it] = User(id = it)
        }
        return result
    }

    override fun createUserRepresentation(userId: UUID, idempotencyKey: UUID) {
        cache.get(idempotencyKey) {
            userRepository.save(
                UserRepresentation(
                    id = userId,
                    shouldBePersisted = true,
                )
            )
        }
    }

    override fun deleteUserRepresentation(userId: UUID, idempotencyKey: UUID) {
        cache.get(idempotencyKey) {
            userRepository.deleteById(userId)
        }
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

        val updatedUser = userRepository.save(
            presentUser.copy(
                features = presentUser.features.toggle(
                    Feature.POSTS_DISABLED
                )
            )
        )
        return !updatedUser.features.check(Feature.POSTS_DISABLED)
    }
}
