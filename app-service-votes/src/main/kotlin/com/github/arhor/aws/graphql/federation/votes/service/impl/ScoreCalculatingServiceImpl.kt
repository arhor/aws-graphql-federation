package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.votes.data.repository.CommentRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.data.repository.PostRepresentationRepository
import com.github.arhor.aws.graphql.federation.votes.data.repository.VoteEntityRepository
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteEntityType
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteValue
import com.github.arhor.aws.graphql.federation.votes.service.ScoreCalculatingService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

@Trace
@Service
class ScoreCalculatingServiceImpl(
    private val voteRepository: VoteEntityRepository,
    private val postRepository: PostRepresentationRepository,
    private val commentRepository: CommentRepresentationRepository,
) : ScoreCalculatingService {

    override fun calculateHotScore(upvotes: Int, downvotes: Int, createdAt: LocalDateTime): Double {
        val netScore = upvotes - downvotes
        val absScore = max(abs(netScore), MINIMUM_SCORE)
        val logScore = log10(absScore.toDouble())

        val ageInSeconds = Duration.between(createdAt, LocalDateTime.now(ZoneOffset.UTC)).seconds
        val timeScore = ageInSeconds / TIME_DECAY_FACTOR

        return logScore + timeScore
    }

    @Transactional
    override fun updateScore(entityId: UUID, entityType: VoteEntityType, createdAt: LocalDateTime) {
        val (upvotes, downvotes) = calculateVotes(entityId, entityType)
        val score = calculateHotScore(upvotes, downvotes, createdAt)

        when (entityType) {
            VoteEntityType.POST -> {
                val post = postRepository.findByIdOrNull(entityId)
                    ?: throw IllegalStateException("Post not found: $entityId")
                postRepository.save(post.copy(score = score))
            }

            VoteEntityType.COMMENT -> {
                val comment = commentRepository.findByIdOrNull(entityId)
                    ?: throw IllegalStateException("Comment not found: $entityId")
                commentRepository.save(comment.copy(score = score))
            }
        }
    }

    private fun calculateVotes(
        entityId: UUID,
        entityType: VoteEntityType
    ): Pair<Int, Int> {
        voteRepository.findAllByEntityIdAndEntityType(entityId, entityType).use { votes ->
            var upvotes = 0
            var downvotes = 0
            for (vote in votes) {
                when (vote.value) {
                    VoteValue.INC -> upvotes++
                    VoteValue.DEC -> downvotes++
                }
            }
            return upvotes to downvotes
        }
    }

    companion object {
        /**
         * The minimum score value to prevent taking log of zero or negative numbers.
         * This ensures that even posts with no votes or negative net score have a chance to appear in rankings.
         */
        private const val MINIMUM_SCORE = 1

        /**
         * Time decay factor in seconds (12.5 hours).
         * This factor determines how quickly posts lose their ranking over time.
         * A larger value means posts stay relevant longer, while a smaller value means they decay faster.
         */
        private const val TIME_DECAY_FACTOR = 45000.0
    }
} 
