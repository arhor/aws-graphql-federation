package com.github.arhor.dgs.users.data.repository

import com.github.arhor.dgs.users.data.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository :
    CrudRepository<UserEntity, Long>,
    PagingAndSortingRepository<UserEntity, Long> {

    fun existsByUsername(username: String): Boolean
}
