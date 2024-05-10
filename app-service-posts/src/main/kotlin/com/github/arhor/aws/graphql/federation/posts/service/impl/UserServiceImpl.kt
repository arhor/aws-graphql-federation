package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserService
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
class UserServiceImpl(
    private val cacheManager: CacheManager,
    private val userRepository: UserRepository,
) : UserService {

    private lateinit var cache: Cache

    @PostConstruct
    fun initialize() {
        cache = cacheManager[Caches.IDEMPOTENT_ID_SET]
    }

    override fun findInternalUserRepresentation(userId: UUID): User {
        return userRepository.findByIdOrNull(userId)?.let(::mapEntityToUser)
            ?: throw EntityNotFoundException(
                DgsConstants.USER.TYPE_NAME,
                "${DgsConstants.USER.Id} = $userId",
                Operation.LOOKUP,
            )
    }

    override fun createInternalUserRepresentation(userId: UUID, idempotencyId: UUID) {
        cache.get(idempotencyId) {
            userRepository.save(UserEntity(id = userId))
        }
    }

    override fun deleteInternalUserRepresentation(userId: UUID, idempotencyId: UUID) {
        cache.get(idempotencyId) {
            userRepository.deleteById(userId)
        }
    }

    private fun mapEntityToUser(entity: UserEntity): User {
        return User(
            id = entity.id,
        )
    }
}
