package com.github.arhor.aws.graphql.federation.posts.data.converter

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
object OptionsWritingConverter : Converter<PostEntity.Options, Long> {

    override fun convert(source: PostEntity.Options): Long =
        source.items.fold(0) { result, item -> result or (1L shl item.ordinal) }
}
