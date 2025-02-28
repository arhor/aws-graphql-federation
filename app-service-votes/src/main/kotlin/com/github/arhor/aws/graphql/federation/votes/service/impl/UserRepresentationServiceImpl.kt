package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.model.UserRepresentation
import com.github.arhor.aws.graphql.federation.votes.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.votes.service.UserRepresentationService
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class UserRepresentationServiceImpl(
    private val userRepresentationRepository: UserRepresentationRepository,
    private val baseRepresentationOperations: BaseRepresentationOperations,
) : UserRepresentationService {

    override fun findUsersRepresentationsInBatch(userIds: Set<UUID>): Map<UUID, User> =
        with(userRepresentationRepository) {
            baseRepresentationOperations.findRepresentationsInBatch(
                keys = userIds,
                onPresent = { row -> User(id = row.id) },
                onMissing = { key -> User(id = key) },
            )
        }

    @Cacheable(cacheNames = ["create-user-representation-requests-cache"])
    override fun createUserRepresentation(userId: UUID, idempotencyKey: UUID): Unit =
        with(userRepresentationRepository) {
            baseRepresentationOperations.createRepresentationInDB(
                UserRepresentation(
                    id = userId,
                    shouldBePersisted = true,
                )
            )
        }

    @Cacheable(cacheNames = ["delete-user-representation-requests-cache"])
    override fun deleteUserRepresentation(userId: UUID, idempotencyKey: UUID): Unit =
        with(userRepresentationRepository) {
            baseRepresentationOperations.deleteRepresentationInDB(
                UserRepresentation(
                    id = userId,
                    shouldBePersisted = false,
                )
            )
        }
}
