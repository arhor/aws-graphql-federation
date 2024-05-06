package com.github.arhor.aws.graphql.federation.posts.service.mapping

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Option

interface OptionsMapper {
    fun mapIntoList(options: PostEntity.Options): List<Option>
    fun mapFromList(options: List<Option>?): PostEntity.Options
}
