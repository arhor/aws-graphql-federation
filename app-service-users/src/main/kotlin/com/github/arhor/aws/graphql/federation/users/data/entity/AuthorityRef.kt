package com.github.arhor.aws.graphql.federation.users.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(AuthorityRef.TABLE_NAME)
@Immutable
data class AuthorityRef(
    @Id
    @Column(COL_ID)
    val id: Long? = null,

    @Column(COL_AUTH_ID)
    val tagId: AggregateReference<AuthorityEntity, Long>,
) {
    companion object {
        const val TABLE_NAME = "users_has_authorities"

        const val COL_ID = "id"
        const val COL_USER_ID = "user_id"
        const val COL_AUTH_ID = "auth_id"

        fun create(entity: AuthorityEntity) = AuthorityRef(
            tagId = AggregateReference.to(
                entity.id ?: throw IllegalStateException("Referenced entity must be persisted")
            )
        )
    }
}
