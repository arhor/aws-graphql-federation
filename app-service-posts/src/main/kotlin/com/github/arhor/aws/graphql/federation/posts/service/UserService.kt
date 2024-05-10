package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import java.util.UUID

interface UserService {
    fun findInternalUserRepresentation(userId: UUID): User
    fun createInternalUserRepresentation(userId: UUID, idempotencyKey: UUID)
    fun deleteInternalUserRepresentation(userId: UUID, idempotencyKey: UUID)
}
