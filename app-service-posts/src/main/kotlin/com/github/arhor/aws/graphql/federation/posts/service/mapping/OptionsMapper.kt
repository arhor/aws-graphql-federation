package com.github.arhor.aws.graphql.federation.posts.service.mapping

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.dgs.posts.generated.graphql.types.Option

interface OptionsMapper {
    fun map(options: PostEntity.Options): List<Option>
    fun map(options: List<Option>?): PostEntity.Options
}
