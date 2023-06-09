package com.github.arhor.dgs.topics.data.repository

import com.github.arhor.dgs.topics.data.entity.PostEntity
import org.springframework.data.repository.CrudRepository

interface PostRepository :
    CrudRepository<PostEntity, Long> {

    fun findAllByUserIdIn(userIds: Collection<String>): List<PostEntity>

    fun findAllByTopicIdIn(topicIds: Collection<String>): List<PostEntity>

    fun findAllByUserId(userId: String): List<PostEntity>

    fun findAllByTopicId(topicId: String): List<PostEntity>
}
