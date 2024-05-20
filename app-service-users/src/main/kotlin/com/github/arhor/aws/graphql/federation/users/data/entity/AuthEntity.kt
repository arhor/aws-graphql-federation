package com.github.arhor.aws.graphql.federation.users.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(AuthEntity.TABLE_NAME)
@Immutable
data class AuthEntity(
    @Id
    @Column("id")
    val id: Int? = null,

    @Column("name")
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "authorities"
    }
}
