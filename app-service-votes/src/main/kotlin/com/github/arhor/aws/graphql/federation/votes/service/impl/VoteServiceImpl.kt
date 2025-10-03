package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import com.github.arhor.aws.graphql.federation.starter.security.ensureAccessAllowed
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.repository.CommentRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.data.repository.PostRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.data.repository.VoteEntityRepository
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.DgsConstants
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.DgsConstants.VOTE
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.CreateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.UpdateVoteInput
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.Vote
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteEntityType
import com.github.arhor.aws.graphql.federation.votes.service.ScoreCalculatingService
import com.github.arhor.aws.graphql.federation.votes.service.VoteService
import com.github.arhor.aws.graphql.federation.votes.service.mapping.VoteMapper
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Trace
@Service
class VoteServiceImpl(
    private val voteMapper: VoteMapper,
    private val voteRepository: VoteEntityRepository,
    private val postRepository: PostRepresentationRepository,
    private val commentRepository: CommentRepresentationRepository,
    private val scoreCalculatingService: ScoreCalculatingService,
) : VoteService {

    @Transactional
    override fun createVote(input: CreateVoteInput, actor: CurrentUserDetails): Vote {
        val entityUserId = when (input.entityType) {
            VoteEntityType.POST -> {
                val post = postRepository.findByIdOrNull(input.entityId)
                    ?: throw EntityNotFoundException(
                        entity = DgsConstants.POST.TYPE_NAME,
                        condition = "id = ${input.entityId}",
                        operation = Operation.CREATE,
                    )
                post.userId
            }

            VoteEntityType.COMMENT -> {
                val comment = commentRepository.findByIdOrNull(input.entityId)
                    ?: throw EntityNotFoundException(
                        entity = DgsConstants.COMMENT.TYPE_NAME,
                        condition = "id = ${input.entityId}",
                        operation = Operation.CREATE,
                    )
                comment.userId
            }
        }
        if (actor.id == entityUserId) {
            logger.info("An illegal attempt to create vote {}, by the user {}", input, actor)
            throw AccessDeniedException("It's forbidden to vote for the own ${input.entityType.name.lowercase()}")
        }

        voteRepository.existsByUserIdAndEntityIdAndEntityType(
            userId = actor.id,
            entityId = input.entityId,
            entityType = input.entityType
        ).also { exists ->
            if (exists) {
                throw EntityDuplicateException(
                    entity = VOTE.TYPE_NAME,
                    condition = "${VOTE.UserId} = ${actor.id}, ${VOTE.EntityType} = ${input.entityType}",
                    operation = Operation.CREATE,
                )
            }
        }

        val vote = voteMapper.mapToEntity(input, actor.id)
        val savedVote = voteRepository.save(vote)

        when (input.entityType) {
            VoteEntityType.POST -> postRepository.findByIdOrNull(input.entityId)?.let {
                scoreCalculatingService.updateScore(
                    entityId = input.entityId,
                    entityType = input.entityType,
                    createdAt = it.createdDateTime ?: LocalDateTime.now()
                )
            }

            VoteEntityType.COMMENT -> commentRepository.findByIdOrNull(input.entityId)?.let {
                scoreCalculatingService.updateScore(
                    entityId = input.entityId,
                    entityType = input.entityType,
                    createdAt = it.createdDateTime ?: LocalDateTime.now()
                )
            }
        }
        return voteMapper.mapToVote(savedVote)
    }

    @Transactional
    override fun updateVote(input: UpdateVoteInput, actor: CurrentUserDetails): Vote {
        val vote = voteRepository.findByIdOrNull(input.id)
            ?: throw VoteNotFoundException.byId(id = input.id, operation = Operation.UPDATE)

        ensureAccessAllowed(vote.userId, actor)

        val updatedVote = vote.copy(value = input.value)
        val savedVote = try {
            voteRepository.save(updatedVote)
        } catch (e: OptimisticLockingFailureException) {
            logger.warn("Optimistic locking failure while updating vote: {}", e.message)
            throw VoteNotFoundException.byId(id = input.id, operation = Operation.UPDATE, cause = e)
        }
        when (vote.entityType) {
            VoteEntityType.POST -> postRepository.findByIdOrNull(vote.entityId)?.let {
                scoreCalculatingService.updateScore(
                    entityId = vote.entityId,
                    entityType = vote.entityType,
                    createdAt = it.createdDateTime ?: LocalDateTime.now()
                )
            }

            VoteEntityType.COMMENT -> commentRepository.findByIdOrNull(vote.entityId)?.let {
                scoreCalculatingService.updateScore(
                    entityId = vote.entityId,
                    entityType = vote.entityType,
                    createdAt = it.createdDateTime ?: LocalDateTime.now()
                )
            }
        }
        return voteMapper.mapToVote(savedVote)
    }

    @Transactional
    override fun deleteVote(id: UUID, actor: CurrentUserDetails): Boolean {
        val vote = voteRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(
                entity = VOTE.TYPE_NAME,
                condition = "id = $id",
                operation = Operation.DELETE
            )

        ensureAccessAllowed(vote.userId, actor)

        voteRepository.delete(vote)

        when (vote.entityType) {
            VoteEntityType.POST -> postRepository.findByIdOrNull(vote.entityId)?.let {
                scoreCalculatingService.updateScore(
                    entityId = vote.entityId,
                    entityType = vote.entityType,
                    createdAt = it.createdDateTime ?: LocalDateTime.now()
                )
            }

            VoteEntityType.COMMENT -> commentRepository.findByIdOrNull(vote.entityId)?.let {
                scoreCalculatingService.updateScore(
                    entityId = vote.entityId,
                    entityType = vote.entityType,
                    createdAt = it.createdDateTime ?: LocalDateTime.now()
                )
            }
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(VoteServiceImpl::class.java)
    }
} 
