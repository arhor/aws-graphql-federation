package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.AuthEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.mapping.UserIdToAuthNamesResultSetExtractor
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface AuthRepository : CrudRepository<AuthEntity, Long> {

    @Query(
        name = "AuthorityEntity.findAllByUserIdIn",
        resultSetExtractorRef = UserIdToAuthNamesResultSetExtractor.BEAN_NAME,
    )
    fun findAllByUserIdIn(postIds: Collection<Long>): Map<Long, List<String>>
}
