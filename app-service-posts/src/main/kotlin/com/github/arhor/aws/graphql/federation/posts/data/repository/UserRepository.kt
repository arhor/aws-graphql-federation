package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface UserRepository : CrudRepository<UserEntity, UUID>
