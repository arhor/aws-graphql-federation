package com.github.arhor.aws.graphql.federation.users.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.api.graphql.dataloader.AuthBatchLoader
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

    /* Queries */

    @DgsData(parentType = USER.TYPE_NAME)
    fun authorities(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<String>> {
        val loader = dfe.getDataLoader<UUID, List<String>>(AuthBatchLoader::class.java)
        val source = dfe.getSource<User>()

        return loader.load(source.id)
    }
}
