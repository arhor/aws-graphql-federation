package com.github.arhor.aws.graphql.federation.users.data.repository

import com.github.arhor.aws.graphql.federation.users.data.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.UUID

interface UserRepository :
    CrudRepository<UserEntity, UUID>,
    PagingAndSortingRepository<UserEntity, UUID> {

    fun findByUsername(username: String): UserEntity?
    fun existsByUsername(username: String): Boolean
}
