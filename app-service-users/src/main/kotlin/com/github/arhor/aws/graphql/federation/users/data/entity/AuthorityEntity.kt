package com.github.arhor.aws.graphql.federation.users.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(AuthorityEntity.TABLE_NAME)
@Immutable
data class AuthorityEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("name")
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "authorities"
    }
}
