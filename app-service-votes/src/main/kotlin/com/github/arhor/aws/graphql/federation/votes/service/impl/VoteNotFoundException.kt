package com.github.arhor.aws.graphql.federation.votes.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.DgsConstants.VOTE
import java.util.UUID

@Suppress("unused")
class VoteNotFoundException : EntityNotFoundException {

    @JvmOverloads
    constructor(
        condition: String,
        operation: Operation = Operation.UNKNOWN,
        cause: Exception? = null
    ) : super(VOTE.TYPE_NAME, condition, operation, cause)

    @JvmOverloads
    constructor(
        condition: Map<String, Any?>,
        operation: Operation = Operation.UNKNOWN,
        cause: Exception? = null
    ) : super(VOTE.TYPE_NAME, condition, operation, cause)

    companion object {
        @JvmStatic
        fun byId(
            id: UUID,
            operation: Operation = Operation.UNKNOWN,
            cause: Exception? = null,
        ): VoteNotFoundException = VoteNotFoundException(mapOf(VOTE.Id to id), operation, cause)
    }
}
