package com.github.arhor.dgs.topics.service

import com.github.arhor.dgs.topics.generated.graphql.types.CreatePostRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Post

interface PostService {
    fun createNewPost(request: CreatePostRequest): Post
    fun getPostsByUserIds(userIds: Set<Long>): Map<Long, List<Post>>
    fun getPostsByTopicIds(topicIds: Set<Long>): Map<Long, List<Post>>
    fun getPostsUserId(userId: Long): List<Post>
    fun getPostsByTopicId(topicId: Long): List<Post>
}
