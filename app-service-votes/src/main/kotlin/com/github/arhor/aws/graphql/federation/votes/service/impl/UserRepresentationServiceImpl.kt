package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.model.UserRepresentation
import com.github.arhor.aws.graphql.federation.votes.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.service.UserRepresentationService
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class UserRepresentationServiceImpl(
    private val userRepository: UserRepresentationRepository,
) : UserRepresentationService {

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
}
