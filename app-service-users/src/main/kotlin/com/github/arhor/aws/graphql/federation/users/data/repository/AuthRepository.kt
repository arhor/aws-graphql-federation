package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.starter.security.PredefinedAuthority
import com.github.arhor.aws.graphql.federation.users.data.model.AuthEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.mapping.UserIdToAuthNamesResultSetExtractor
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface AuthRepository : CrudRepository<AuthEntity, Int> {

    @Query(
        name = "AuthorityEntity.findAllByUserIdIn",
        resultSetExtractorRef = UserIdToAuthNamesResultSetExtractor.BEAN_NAME,
    )
    fun findAllByUserIdIn(userIds: Collection<UUID>): Map<UUID, List<String>>

    fun findByName(name: PredefinedAuthority): AuthEntity
}
