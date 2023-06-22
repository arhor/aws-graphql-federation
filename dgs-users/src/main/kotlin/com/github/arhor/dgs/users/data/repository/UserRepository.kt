package com.github.arhor.dgs.users.data.repository

import com.github.arhor.dgs.users.data.entity.UserEntity
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository :
    CrudRepository<UserEntity, Long>,
    PagingAndSortingRepository<UserEntity, Long> {

    fun findByUsername(username: String): UserEntity?

    fun existsByUsername(username: String): Boolean

    @Modifying
    @Query(value = "DELETE FROM ${UserEntity.TABLE_NAME} e WHERE e.id = :userId")
    fun deleteByIdReturningNumberRecordsAffected(userId: Long): Int
}
