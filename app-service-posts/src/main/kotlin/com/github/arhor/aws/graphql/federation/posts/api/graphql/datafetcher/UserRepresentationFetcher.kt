package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.getUuid
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.SwitchUserPostsInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class UserRepresentationFetcher(
    private val userService: UserRepresentationService,
) {

    /* ---------- Entity Fetchers ---------- */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun resolveUser(values: Map<String, Any>): User =
        userService.findUserRepresentation(
            userId = values.getUuid(USER.Id)
        )

    /* ---------- Mutations ---------- */

    @DgsMutation
    fun switchUserPosts(@InputArgument input: SwitchUserPostsInput): Boolean {
        return userService.switchPosts(input)
    }
}
