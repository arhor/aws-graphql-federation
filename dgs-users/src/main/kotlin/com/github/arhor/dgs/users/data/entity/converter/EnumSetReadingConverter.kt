package com.github.arhor.dgs.users.data.entity.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.EnumSet

@ReadingConverter
class EnumSetReadingConverter<T>(
    private val clazz: Class<T>,
    private val index: T.() -> Int = { ordinal },
) : Converter<Int, EnumSet<T>> where T : Enum<T> {

    override fun convert(source: Int): EnumSet<T> =
        EnumSet.noneOf(clazz).apply {
            for (item in EnumSet.allOf(clazz)) {
                if ((source and (1 shl item.index())) != 0) {
                    add(item)
                }
            }
        }
}

