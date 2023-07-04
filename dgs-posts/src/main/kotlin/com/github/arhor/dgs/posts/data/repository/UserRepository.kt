package com.github.arhor.dgs.posts.data.repository

import com.github.arhor.dgs.posts.data.entity.UserEntity
import org.springframework.data.repository.ListCrudRepository

interface UserRepository : ListCrudRepository<UserEntity, Long>
