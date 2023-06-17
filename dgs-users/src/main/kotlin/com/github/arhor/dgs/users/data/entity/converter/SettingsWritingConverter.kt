package com.github.arhor.dgs.users.data.entity.converter

import com.github.arhor.dgs.users.data.entity.Setting
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.util.EnumSet

@WritingConverter
object SettingsWritingConverter : Converter<EnumSet<Setting>, Long> {

    override fun convert(source: EnumSet<Setting>): Long {
        var result = 0L
        for (setting in source) {
            result = result or (1L shl setting.index)
        }
        return result
    }
}
