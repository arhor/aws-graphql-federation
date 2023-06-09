package com.github.arhor.dgs.topics.data.repository

import com.github.arhor.dgs.topics.data.entity.TopicEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface TopicRepository :
    CrudRepository<TopicEntity, Long>,
    PagingAndSortingRepository<TopicEntity, Long>
