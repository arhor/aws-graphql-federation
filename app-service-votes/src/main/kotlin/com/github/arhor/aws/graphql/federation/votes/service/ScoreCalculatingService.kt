package com.github.arhor.aws.graphql.federation.votes.service

import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.VoteEntityType
import java.time.LocalDateTime
import java.util.UUID

interface ScoreCalculatingService {
    /**
     * Calculates the "Hot" score for a post or comment based on Reddit's algorithm.
     * The formula is: score = log10(max(abs(upvotes - downvotes), 1)) + (seconds / 45000)
     * where:
     * - upvotes - downvotes is the net score
     * - seconds is the age of the post in seconds
     * - 45000 is the time decay factor (12.5 hours)
     *
     * @param upvotes   number of upvotes
     * @param downvotes number of downvotes
     * @param createdAt creation timestamp
     * @return the calculated hot score
     */
    fun calculateHotScore(upvotes: Int, downvotes: Int, createdAt: LocalDateTime): Double

    /**
     * Updates the score for a post or comment.
     *
     * @param entityId   the ID of the post or comment
     * @param entityType the type of entity
     * @param createdAt  the creation timestamp of the entity
     */
    fun updateScore(entityId: UUID, entityType: VoteEntityType, createdAt: LocalDateTime)
} 
