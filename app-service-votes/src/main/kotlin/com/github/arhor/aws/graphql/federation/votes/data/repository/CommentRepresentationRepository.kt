package com.github.arhor.aws.graphql.federation.votes.data.repository

import com.github.arhor.aws.graphql.federation.votes.data.model.CommentRepresentation
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface CommentRepresentationRepository : CrudRepository<CommentRepresentation, UUID>
