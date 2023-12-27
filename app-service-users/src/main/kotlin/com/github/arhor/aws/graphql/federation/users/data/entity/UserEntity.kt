package com.github.arhor.aws.graphql.federation.users.data.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(UserEntity.TABLE_NAME)
@Immutable
data class UserEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("username")
    val username: String,

    @Column("password")
    val password: String,

    @Version
    @Column("version")
    val version: Long? = null,

    @CreatedDate
    @Column("created_date_time")
    val createdDateTime: LocalDateTime? = null,

    @LastModifiedDate
    @Column("updated_date_time")
    val updatedDateTime: LocalDateTime? = null,

    @MappedCollection(idColumn = AuthorityRef.COL_USER_ID)
    val authorities: Set<AuthorityRef> = emptySet()
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}
