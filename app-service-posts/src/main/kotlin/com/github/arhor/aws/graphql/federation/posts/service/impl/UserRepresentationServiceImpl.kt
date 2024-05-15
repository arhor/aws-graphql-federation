package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
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

    override fun findUserRepresentation(userId: UUID): User =
        userRepresentationRepository
            .findByIdOrNull(userId)
            .let { mapEntityToUser(userId, it) }

    override fun createUserRepresentation(userId: UUID, idempotencyKey: UUID) {
        cache.get(idempotencyKey) {
            userRepresentationRepository.save(UserRepresentation(id = userId))
        }
    }

    override fun deleteUserRepresentation(userId: UUID, idempotencyKey: UUID) {
        cache.get(idempotencyKey) {
            userRepresentationRepository.deleteById(userId)
        }
    }

    private fun mapEntityToUser(userId: UUID, user: UserRepresentation?): User {
        return User(
            id = user?.id ?: userId,
            postsOperable = user != null,
        )
    }
}
