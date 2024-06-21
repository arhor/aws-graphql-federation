package com.github.arhor.aws.graphql.federation.posts.data.repository.sorting

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import org.springframework.data.domain.Sort

object Posts {
    val sortedByCreatedDateTimeDesc = Sort.by(Sort.Direction.DESC, PostEntity::createdDateTime.name)
}
