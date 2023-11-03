package com.github.arhor.aws.graphql.federation.posts.data.converter

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Option
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.EnumSet

@ReadingConverter
object EnumSetReadingConverter : Converter<Long, PostEntity.Options> {

    override fun convert(source: Long): PostEntity.Options =
        PostEntity.Options(
            items = EnumSet.noneOf(Option::class.java).apply {
                if (source != 0L) {
                    for (item in EnumSet.allOf(Option::class.java)) {
                        if ((source and (1L shl item.ordinal)) != 0L) {
                            add(item)
                        }
                    }
                }
            }
        )
}
