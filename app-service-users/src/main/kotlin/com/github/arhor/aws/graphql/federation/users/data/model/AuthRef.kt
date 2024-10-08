package com.github.arhor.aws.graphql.federation.users.data.model

import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(AuthRef.TABLE_NAME)
@Immutable
data class AuthRef(
    @Column(COL_AUTH_ID)
    val authId: Int,
) {
    companion object {
        const val TABLE_NAME = "users_have_authorities"

        // @formatter:off
        const val COL_USER_ID = "user_id"
        const val COL_AUTH_ID = "auth_id"
        // @formatter:on

        fun from(entity: AuthEntity) = AuthRef(
            authId = entity.id ?: throw IllegalStateException("Referenced entity must have an ID")
        )
    }
}
