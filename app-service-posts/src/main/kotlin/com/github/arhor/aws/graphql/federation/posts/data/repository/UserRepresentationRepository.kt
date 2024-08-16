package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.model.UserRepresentation
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface UserRepresentationRepository : CrudRepository<UserRepresentation, UUID>
