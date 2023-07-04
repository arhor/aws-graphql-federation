package com.github.arhor.dgs.comments.data.repository

import com.github.arhor.dgs.comments.data.entity.PostEntity
import org.springframework.data.repository.CrudRepository

interface PostRepository : CrudRepository<PostEntity, Long>
