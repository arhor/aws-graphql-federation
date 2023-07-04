package com.github.arhor.dgs.comments.api.graphql.datafetcher

import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.POST
import com.github.arhor.dgs.comments.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.comments.generated.graphql.types.Post
import com.github.arhor.dgs.comments.generated.graphql.types.User
import com.github.arhor.dgs.comments.service.PostService
import com.github.arhor.dgs.comments.service.UserService
import com.github.arhor.dgs.lib.getLong
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEntityFetcher

@DgsComponent
class FederatedEntityFetchers(
    private val userService: UserService,
    private val postService: PostService,
) {

    /* Entity Fetchers */

    @DgsEntityFetcher(name = USER.TYPE_NAME)
    fun fetchUser(values: Map<String, Any>): User? =
        userService.getUserById(
            userId = values.getLong(USER.Id)
        )

    @DgsEntityFetcher(name = POST.TYPE_NAME)
    fun fetchPost(values: Map<String, Any>): Post? =
        postService.getPostById(
            postId = values.getLong(POST.Id)
        )
}
