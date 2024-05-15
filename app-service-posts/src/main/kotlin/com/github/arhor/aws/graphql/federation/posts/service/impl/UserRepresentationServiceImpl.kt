package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentationEntity
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
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
    private val userRepresentationRepository: UserRepresentationRepository,
) : UserRepresentationService {

    private lateinit var cache: Cache

    @PostConstruct
    fun initialize() {
        cache = cacheManager[Caches.IDEMPOTENT_ID_SET]
    }

    override fun findUserRepresentation(userId: UUID): User {
        return userRepresentationRepository.findByIdOrNull(userId)?.let(::mapEntityToUser)
            ?: throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = $userId",
                operation = Operation.LOOKUP,
            )
    }

    override fun createUserRepresentation(userId: UUID, idempotencyKey: UUID) {
        cache.get(idempotencyKey) {
            userRepresentationRepository.save(UserRepresentationEntity(id = userId))
        }
    }

    override fun deleteUserRepresentation(userId: UUID, idempotencyKey: UUID) {
        cache.get(idempotencyKey) {
            userRepresentationRepository.deleteById(userId)
        }
    }

    private fun mapEntityToUser(entity: UserRepresentationEntity): User {
        return User(
            id = entity.id,
        )
    }
}
