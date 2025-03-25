package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import com.github.arhor.aws.graphql.federation.starter.security.ensureAccessAllowed
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.model.VoteEntity
import com.github.arhor.aws.graphql.federation.votes.data.repository.CommentRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.data.repository.PostRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.data.repository.VoteEntityRepository
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.DgsConstants
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.DgsConstants.VOTE
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.CreateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.UpdateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Vote
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteEntityType
import com.github.arhor.aws.graphql.federation.votes.service.VoteService
import com.github.arhor.aws.graphql.federation.votes.service.mapping.VoteMapper
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Trace
@Service
class VoteServiceImpl(
    private val voteMapper: VoteMapper,
    private val voteRepository: VoteEntityRepository,
    private val postRepresentationRepository: PostRepresentationRepository,
    private val commentRepresentationRepository: CommentRepresentationRepository,
) : VoteService {

    @Transactional
    override fun createVote(input: CreateVoteInput, actor: CurrentUserDetails): Vote {
        val entityUserId = when (input.entityType) {
            VoteEntityType.POST -> {
                val post = postRepresentationRepository.findByIdOrNull(input.entityId)
                    ?: throw EntityNotFoundException(
                        entity = DgsConstants.POST.TYPE_NAME,
                        condition = "id = ${input.entityId}",
                        operation = Operation.CREATE
                    )
                post.userId
            }
            VoteEntityType.COMMENT -> {
                val comment = commentRepresentationRepository.findByIdOrNull(input.entityId)
                    ?: throw EntityNotFoundException(
                        entity = DgsConstants.COMMENT.TYPE_NAME,
                        condition = "id = ${input.entityId}",
                        operation = Operation.CREATE
                    )
                comment.userId
            }
        }
        
        ensureAccessAllowed(entityUserId, actor)
        
        val existingVote = voteRepository.findByUserIdAndEntityIdAndEntityType(
            userId = actor.id,
            entityId = input.entityId,
            entityType = VoteEntity.EntityType.valueOf(input.entityType.name)
        )

        if (existingVote != null) {
            return updateVote(
                UpdateVoteInput(
                    id = existingVote.id!!,
                    value = input.value
                ),
                actor
            )
        }

        return voteMapper.mapToEntity(input, actor.id)
            .let(voteRepository::save)
            .let(voteMapper::mapToVote)
    }

    @Transactional
    override fun updateVote(input: UpdateVoteInput, actor: CurrentUserDetails): Vote {
        val initialState = findVoteOrThrow(input.id, Operation.UPDATE)
        ensureAccessAllowed(initialState.userId, actor)
        
        val currentState = initialState.copy(value = input.value)

        return when (currentState != initialState) {
            true -> trySaveHandlingConcurrentUpdates(currentState)
            else -> initialState
        }.let(voteMapper::mapToVote)
    }

    @Transactional
    override fun deleteVote(id: UUID, actor: CurrentUserDetails): Boolean {
        val vote = voteRepository.findByIdOrNull(id) ?: return false
        ensureAccessAllowed(vote.userId, actor)
        
        voteRepository.delete(vote)
        return true
    }

    private fun findVoteOrThrow(id: UUID, operation: Operation): VoteEntity =
        voteRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(
                entity = VOTE.TYPE_NAME,
                condition = "${VOTE.Id} = $id",
                operation = operation
            )

    private fun trySaveHandlingConcurrentUpdates(entity: VoteEntity): VoteEntity {
        return try {
            voteRepository.save(entity)
        } catch (e: OptimisticLockingFailureException) {
            logger.error(e.message, e)

            throw EntityOperationRestrictedException(
                entity = VOTE.TYPE_NAME,
                condition = "${VOTE.Id} = ${entity.id} (updated concurrently)",
                operation = Operation.UPDATE,
                cause = e,
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
} 
