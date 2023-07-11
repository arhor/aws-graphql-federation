package com.github.arhor.dgs.posts.service.mapping.impl

import com.github.arhor.dgs.posts.data.entity.PostEntity
import com.github.arhor.dgs.posts.generated.graphql.types.Option
import com.github.arhor.dgs.posts.service.mapping.OptionsMapper
import org.springframework.stereotype.Component
import java.util.EnumSet

@Component
class OptionsMapperImpl : OptionsMapper {

    override fun map(options: PostEntity.Options): List<Option> {
        return options.items.toList()
    }

    override fun map(options: List<Option>?): PostEntity.Options {
        return PostEntity.Options(
            items = EnumSet.noneOf(Option::class.java).apply {
                if (options != null) {
                    addAll(options)
                }
            }
        )
    }
}
