package com.github.arhor.dgs.users.data.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.EnumSet

@Immutable
@Table(UserEntity.TABLE_NAME)
data class UserEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("username")
    val username: String,

    @Column("password")
    val password: String,

    @Column("settings")
    val settings: EnumSet<Setting> = Setting.emptySet(),

    @Version
    @Column("version")
    val version: Long? = null,

    @CreatedDate
    @Column("created_date_time")
    val createdDateTime: LocalDateTime? = null,

    @LastModifiedDate
    @Column("updated_date_time")
    val updatedDateTime: LocalDateTime? = null,
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}
