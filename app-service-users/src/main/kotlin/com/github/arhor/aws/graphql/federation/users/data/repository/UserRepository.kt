package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository :
    CrudRepository<UserEntity, Long>,
    PagingAndSortingRepository<UserEntity, Long> {

    fun findByUsername(username: String): UserEntity?
    fun existsByUsername(username: String): Boolean
}
