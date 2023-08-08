package com.github.arhor.dgs.comments.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(UserEntity.TABLE_NAME)
@Immutable
data class UserEntity(
    @Id
    @Column(COL_ID)
    val id: Long,
) {
    companion object {
        const val TABLE_NAME = "users"
        const val COL_ID = "id"
    }
}
