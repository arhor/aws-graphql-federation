package com.github.arhor.dgs.users.data.entity.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.util.EnumSet

@WritingConverter
class EnumSetWritingConverter<T>(
    private val clazz: Class<T>,
    private val index: T.() -> Int = { ordinal },
) : Converter<EnumSet<T>, Int> where T : Enum<T> {

    override fun convert(source: EnumSet<T>): Int =
        source.fold(0) { result, item -> result or (1 shl item.index()) }
}
