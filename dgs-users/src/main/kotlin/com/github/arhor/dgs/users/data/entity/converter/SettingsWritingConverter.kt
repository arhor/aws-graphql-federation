package com.github.arhor.dgs.users.data.entity.converter

import com.github.arhor.dgs.users.generated.graphql.types.Setting
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.util.EnumSet

@WritingConverter
object SettingsWritingConverter : Converter<EnumSet<Setting>, Long> {

    override fun convert(source: EnumSet<Setting>): Long =
        source.fold(0L) { result, setting -> result or (1L shl setting.ordinal) }
}
