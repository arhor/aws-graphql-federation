package com.github.arhor.aws.graphql.federation.posts.service

import java.util.UUID

/**
 * Service interface for handling tags.
 */
interface TagService {

    /**
     * Retrieves tags for the specified post IDs.
     *
     * @param postIds the set of post IDs to retrieve tags for
     * @return a map where the key is the post ID and the value is a list of tags associated with that post
     */
    fun getTagsByPostIds(postIds: Set<UUID>): Map<UUID, List<String>>
}
