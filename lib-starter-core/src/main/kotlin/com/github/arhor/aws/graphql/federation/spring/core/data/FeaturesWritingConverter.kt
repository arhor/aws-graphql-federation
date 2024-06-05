package com.github.arhor.aws.graphql.federation.spring.core.data

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class FeaturesWritingConverter<F : Enum<F>>(
    val type: Class<F>,
) : Converter<Features<F>, Int> {

    override fun convert(source: Features<F>): Int =
        source.items.fold(0) { result, item -> result or (1 shl item.ordinal) }
}
