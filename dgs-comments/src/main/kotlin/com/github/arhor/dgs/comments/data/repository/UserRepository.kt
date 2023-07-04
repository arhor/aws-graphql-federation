package com.github.arhor.dgs.comments.data.repository

import com.github.arhor.dgs.comments.data.entity.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long>, WithInsert<UserEntity>
