package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import java.util.UUID

interface UserRepresentationService {
    fun findUserRepresentation(userId: UUID): User
    fun createUserRepresentation(userId: UUID, idempotentKey: UUID)
    fun deleteUserRepresentation(userId: UUID, idempotentKey: UUID)
}
