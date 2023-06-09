package com.github.arhor.dgs.topics.service

import com.github.arhor.dgs.topics.generated.graphql.types.CreatePostRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Post

interface PostService {
    fun createNewPost(request: CreatePostRequest): Post
    fun getPostsByUserIds(userIds: Set<String>): Map<String, List<Post>>
    fun getPostsByTopicIds(topicIds: Set<String>): Map<String, List<Post>>
    fun getPostsUserId(userId: String): List<Post>
    fun getPostsByTopicId(topicId: String): List<Post>
}
