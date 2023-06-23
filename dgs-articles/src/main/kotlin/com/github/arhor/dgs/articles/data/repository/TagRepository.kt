package com.github.arhor.dgs.articles.data.repository

import com.github.arhor.dgs.articles.data.entity.TagEntity
import org.springframework.data.repository.CrudRepository

interface TagRepository : CrudRepository<TagEntity, Long>
