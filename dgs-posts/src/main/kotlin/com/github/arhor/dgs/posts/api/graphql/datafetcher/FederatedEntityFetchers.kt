package com.github.arhor.dgs.posts.api.graphql.datafetcher

import com.github.arhor.dgs.lib.getLong
import com.github.arhor.dgs.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.posts.generated.graphql.types.User
import com.github.arhor.dgs.posts.service.UserService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class FederatedEntityFetchers(
    private val userService: UserService,
) {
    /* Entity Fetchers */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun fetchUser(values: Map<String, Any>): User {
        return userService.getUserById(userId = values.getLong(USER.Id))
    }
}
