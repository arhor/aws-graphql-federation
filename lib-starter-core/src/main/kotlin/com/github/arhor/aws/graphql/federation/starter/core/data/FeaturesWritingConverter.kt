package com.github.arhor.aws.graphql.federation.starter.core.data

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
open class FeaturesWritingConverter : Converter<Features<*, *>, Int> {

    override fun convert(source: Features<*, *>): Int =
        source.items.fold(0) { result, item -> result or (1 shl item.ordinal) }
}
