package com.github.arhor.aws.graphql.federation.votes.data.repository

import com.github.arhor.aws.graphql.federation.votes.data.model.PostRepresentation
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface PostRepresentationRepository : CrudRepository<PostRepresentation, UUID>
