package com.github.arhor.dgs.users.data.repository

import com.github.arhor.dgs.users.data.entity.UserEntity
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository :
    CrudRepository<UserEntity, Long>,
    PagingAndSortingRepository<UserEntity, Long> {

    fun existsByUsername(username: String): Boolean

    /**
     * Delete User entity by its id, returning number entities affected.
     */
    @Modifying
    @Query(value = "DELETE FROM ${UserEntity.TABLE_NAME} e WHERE e.id = :userId")
    fun deleteUserById(userId: Long): Int
}
