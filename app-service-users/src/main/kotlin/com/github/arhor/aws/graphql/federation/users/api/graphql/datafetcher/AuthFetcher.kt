package com.github.arhor.aws.graphql.federation.users.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.users.api.graphql.dataloader.AuthBatchLoader
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import java.util.concurrent.CompletableFuture

@DgsComponent
class AuthFetcher {

    /* Queries */

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME)
    fun authorities(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<String>> {
        val loader = dfe.getDataLoader<Long, List<String>>(AuthBatchLoader::class.java)
        val source = dfe.getSource<User>()

        return loader.load(source.id)
    }
}
