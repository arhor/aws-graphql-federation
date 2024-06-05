package com.github.arhor.aws.graphql.federation.users.infrastructure.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.infrastructure.graphql.dataloader.AuthBatchLoader
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Trace
@DgsComponent
class AuthFetcher {

    /* ---------- Queries ---------- */

    @DgsData(parentType = USER.TYPE_NAME, field = USER.Authorities)
    fun userAuthorities(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<String>> {
        val loader = dfe.getDataLoader<UUID, List<String>>(AuthBatchLoader::class.java)
        val source = dfe.getSource<User>()

        return loader.load(source.id)
    }
}
