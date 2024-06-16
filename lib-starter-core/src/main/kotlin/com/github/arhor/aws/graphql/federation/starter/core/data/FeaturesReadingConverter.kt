package com.github.arhor.aws.graphql.federation.starter.core.data

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.EnumSet

@ReadingConverter
open class FeaturesReadingConverter<F : Features<F, E>, E : Enum<E>>(
    val type: Class<E>,
    val factory: (EnumSet<E>) -> F,
) : Converter<Int, F> {

    override fun convert(source: Int): F =
        factory(
            EnumSet.noneOf(type).apply {
                if (source != 0) {
                    for (item in EnumSet.allOf(type)) {
                        if ((source and (1 shl item.ordinal)) != 0) {
                            add(item)
                        }
                    }
                }
            }
        )
}
