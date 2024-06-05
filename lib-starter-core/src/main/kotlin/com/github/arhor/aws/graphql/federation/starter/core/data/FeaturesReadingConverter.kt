package com.github.arhor.aws.graphql.federation.starter.core.data

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.EnumSet

@ReadingConverter
class FeaturesReadingConverter<F : Enum<F>>(
    val type: Class<F>,
) : Converter<Int, Features<F>> {

    override fun convert(source: Int): Features<F> =
        Features(
            items = EnumSet.noneOf(type).apply {
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
