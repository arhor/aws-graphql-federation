package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.SwitchUserPostsInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.posts.util.Caches
import com.github.arhor.aws.graphql.federation.posts.util.get
import com.github.arhor.aws.graphql.federation.tracing.Trace
import jakarta.annotation.PostConstruct
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
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
                postsDisabled = user.postsDisabled,
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
                    postsDisabled = false,
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

    override fun switchUserPosts(input: SwitchUserPostsInput): Boolean {
        val userId = input.userId
        val shouldBeDisabled = input.disabled

        val user =
            userRepository.findByIdOrNull(userId)
                ?: throw EntityNotFoundException(
                    USER.TYPE_NAME,
                    "${USER.Id} = $userId",
                    Operation.UPDATE
                )

        return if (user.postsDisabled != shouldBeDisabled) {
            userRepository.save(user.copy(postsDisabled = shouldBeDisabled))
            true
        } else {
            false
        }
    }
}
