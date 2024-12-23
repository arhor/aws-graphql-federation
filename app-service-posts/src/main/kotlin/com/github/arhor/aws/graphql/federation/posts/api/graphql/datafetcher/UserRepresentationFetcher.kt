package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.getUuid
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader.UserRepresentationBatchLoader
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsEntityFetcher
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID
import java.util.concurrent.CompletableFuture

@DgsComponent
class UserRepresentationFetcher(
    private val userService: UserRepresentationService,
) {

    /* ---------- Entity Fetchers ---------- */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun resolveUser(values: Map<String, Any>, dfe: DgsDataFetchingEnvironment): CompletableFuture<User> {
        val userId = values.getUuid(USER.Id)
        val loader = dfe.getDataLoader<UUID, User>(UserRepresentationBatchLoader::class.java)

        return loader.load(userId)
    }

    /* ---------- Mutations ---------- */

    @DgsMutation
    @PreAuthorize("hasRole('ADMIN')")
    fun toggleUserPosts(@InputArgument userId: UUID): Boolean {
        return userService.toggleUserPosts(userId)
    }
}
