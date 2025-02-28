package com.github.arhor.aws.graphql.federation.votes.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.getUuid
import com.github.arhor.aws.graphql.federation.votes.api.graphql.dataloader.UserRepresentationBatchLoader
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.votes.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsEntityFetcher
import java.util.UUID
import java.util.concurrent.CompletableFuture

@DgsComponent
class UserRepresentationFetcher {

    /* ---------- Entity Fetchers ---------- */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun resolveUser(values: Map<String, Any>, dfe: DgsDataFetchingEnvironment): CompletableFuture<User> {
        val userId = values.getUuid(USER.Id)
        val loader = dfe.getDataLoader<UUID, User>(UserRepresentationBatchLoader::class.java)

        return loader.load(userId)
    }
}
